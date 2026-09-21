package com.aeriotv.android.core.pairing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Secure local-network playlist transfer for Streamy 3.
 * The receiving device shows a one-time six-digit code. The sending device
 * discovers it on the same LAN, proves knowledge of that code with J-PAKE,
 * then sends one AES-GCM encrypted payload. A successful transfer consumes
 * the code; no Google account or cloud storage is involved.
 */
public final class DevicePairingTransport {
    public static final int TCP_PORT = 38483;
    public static final int UDP_PORT = 38482;
    private static volatile Receiver receiver;

    private DevicePairingTransport() {}

    public interface PayloadListener {
        void onPayload(String payload);
    }

    public static synchronized String startReceiver(PayloadListener listener) {
        stop();
        try {
            Receiver next = new Receiver(listener);
            receiver = next;
            next.start();
            return next.pin;
        } catch (Exception e) {
            return null;
        }
    }

    public static synchronized void stop() {
        Receiver old = receiver;
        receiver = null;
        if (old != null) old.close();
    }

    public static boolean isReceiving() {
        Receiver current = receiver;
        return current != null && current.alive();
    }

    public static boolean send(String pin, String payload, int timeoutMs) {
        if (pin == null || !pin.matches("[0-9]{6}") || payload == null) return false;
        byte[] plain = payload.getBytes(StandardCharsets.UTF_8);
        if (plain.length > DevicePairingCrypto.MAX_PAYLOAD) return false;

        long deadline = System.nanoTime() + Math.max(4000, timeoutMs) * 1_000_000L;
        String nonce = UUID.randomUUID().toString();
        byte[] query = ("S3DISCOVER1 " + nonce).getBytes(StandardCharsets.US_ASCII);
        Set<InetAddress> tried = new HashSet<>();

        try (DatagramSocket udp = new DatagramSocket()) {
            udp.setBroadcast(true);
            udp.setSoTimeout(500);
            while (System.nanoTime() < deadline && tried.size() < 8) {
                udp.send(new DatagramPacket(
                        query, query.length, InetAddress.getByName("255.255.255.255"), UDP_PORT
                ));
                DatagramPacket reply = new DatagramPacket(new byte[128], 128);
                try {
                    udp.receive(reply);
                } catch (SocketTimeoutException e) {
                    continue;
                }
                String response = new String(
                        reply.getData(), 0, reply.getLength(), StandardCharsets.US_ASCII
                );
                if (!response.equals("S3OFFER1 " + nonce) || !tried.add(reply.getAddress())) continue;

                try (Socket socket = new Socket()) {
                    int remaining = (int) Math.min(
                            10000,
                            Math.max(1, (deadline - System.nanoTime()) / 1_000_000L)
                    );
                    socket.connect(
                            new InetSocketAddress(reply.getAddress(), TCP_PORT),
                            Math.min(3000, remaining)
                    );
                    socket.setSoTimeout(remaining);
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    try (DevicePairingCrypto.Session session =
                                 DevicePairingCrypto.exchange(in, out, pin, false)) {
                        DevicePairingCrypto.writeBytes(out, session.encrypt(plain));
                        return true;
                    }
                } catch (Exception ignored) {
                    // Try another discovered receiver until the deadline.
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private static final class Receiver {
        final ServerSocket server;
        final DatagramSocket discovery;
        final String pin = DevicePairingCrypto.newPin();
        final long expires = System.nanoTime() + 120_000_000_000L;
        final PayloadListener listener;
        volatile Socket client;
        volatile boolean closed;
        int attempts;

        Receiver(PayloadListener listener) throws Exception {
            this.listener = listener;
            server = new ServerSocket();
            try {
                server.setReuseAddress(true);
                server.bind(new InetSocketAddress(TCP_PORT));
                server.setSoTimeout(500);
                discovery = new DatagramSocket(UDP_PORT);
                discovery.setSoTimeout(500);
            } catch (Exception e) {
                server.close();
                throw e;
            }
        }

        boolean alive() {
            return !closed && System.nanoTime() < expires;
        }

        void close() {
            closed = true;
            try { server.close(); } catch (Exception ignored) {}
            discovery.close();
            try { if (client != null) client.close(); } catch (Exception ignored) {}
        }

        void start() {
            Thread tcp = new Thread(this::serve, "streamy3-pair-receive");
            tcp.setDaemon(true);
            tcp.start();
            Thread udp = new Thread(this::discover, "streamy3-pair-discovery");
            udp.setDaemon(true);
            udp.start();
        }

        void discover() {
            try {
                while (alive()) {
                    DatagramPacket packet = new DatagramPacket(new byte[128], 128);
                    try {
                        discovery.receive(packet);
                        String request = new String(
                                packet.getData(), 0, packet.getLength(), StandardCharsets.US_ASCII
                        );
                        if (!request.matches("S3DISCOVER1 [0-9a-f-]{36}")) continue;
                        byte[] reply = request.replace("S3DISCOVER1", "S3OFFER1")
                                .getBytes(StandardCharsets.US_ASCII);
                        discovery.send(new DatagramPacket(
                                reply, reply.length, packet.getAddress(), packet.getPort()
                        ));
                    } catch (SocketTimeoutException ignored) {
                    }
                }
            } catch (Exception ignored) {
            } finally {
                close();
            }
        }

        void serve() {
            try {
                while (alive() && attempts < 5) {
                    try (Socket socket = server.accept()) {
                        client = socket;
                        attempts++;
                        socket.setSoTimeout(10000);
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        try (DevicePairingCrypto.Session session =
                                     DevicePairingCrypto.exchange(in, out, pin, true)) {
                            byte[] encrypted = DevicePairingCrypto.readBytes(
                                    in, DevicePairingCrypto.MAX_PAYLOAD + 28
                            );
                            String payload = new String(
                                    session.decrypt(encrypted), StandardCharsets.UTF_8
                            );
                            if (alive() && listener != null) listener.onPayload(payload);
                            break;
                        }
                    } catch (SocketTimeoutException ignored) {
                    } catch (Exception ignored) {
                        // Wrong codes never receive or expose playlist data.
                    } finally {
                        client = null;
                    }
                }
            } finally {
                close();
            }
        }
    }
}
