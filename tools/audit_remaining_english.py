from pathlib import Path
import re

roots=[
    Path("app/src/main/java/com/aeriotv/android/feature"),
    Path("app/src/main/java/com/aeriotv/android/ui"),
    Path("app/src/main/java/com/aeriotv/android/Navigation.kt"),
    Path("app/src/main/java/com/aeriotv/android/MainActivity.kt"),
]
lit=re.compile(r'"((?:\\.|[^"\\])*)"')
hints=(
    "Text(","title =","subtitle =","label =","placeholder","contentDescription",
    "message =","description =","footer =","header =","eyebrow =","badge =",
    "Toast.makeText","TvMenuAction(","TvHeroButton(","HeroRound(","Settings",
    "surfaceCastFailure(","Outcome.Failed(","UpdateState.Error("
)
skip_line=("Log.","Logger.","println(","Regex(","where.append(","jsonPrimitive","SQL","HttpHeaders")
skip_frag=(
    "http://","https://","android.","com.","application/","content://","/api/",
    ".json",".xml",".m3u",".m3u8",".ts",".mp4",".mkv","Bearer ","User-Agent",
    "ACTION_","EXTRA_","BuildConfig","UTF-","yyyy","HH:mm","MM-dd"
)
german_markers=(" der "," die "," das "," und "," ist "," sind "," wird "," werden "," mit "," für "," auf "," von "," zu "," ein "," eine "," aus "," oder "," nicht "," keine "," kein "," Einstellungen","Sender","Filme","Serien","Aufnahme","Wiedergabe","Aktualis","Gerät","Schlüssel","Suche","Zurück","Speichern","Löschen","Deutsch","Übertragung","Bild","Ton","Gruppe","Bibliothek","Poster")
english_markers=(" the "," and "," or "," is "," are "," to "," from "," with "," for "," on "," off "," your "," this "," that "," no "," not "," will "," can "," could "," should "," press "," tap "," select "," choose "," show "," hide "," add "," remove "," delete "," save "," update "," refresh "," search "," channel"," channels"," movie"," movies"," series"," settings"," recording"," recordings"," playlist"," playlists"," guide"," program"," player"," stream"," device"," server"," account"," available"," failed"," loading"," current"," default"," display"," scale"," poster"," library"," daily"," weekly"," every "," launch"," copy "," website"," debug"," diagnostics"," theme"," text size"," time format")
rows=[]
def scan_file(p):
    try: lines=p.read_text(encoding="utf-8").splitlines()
    except: return
    for n,line in enumerate(lines,1):
        sline=line.strip()
        if not any(h in line for h in hints): continue
        if any(x in line for x in skip_line): continue
        for m in lit.finditer(line):
            s=m.group(1)
            if len(s)<2 or not re.search(r"[A-Za-z]",s): continue
            if any(x in s for x in skip_frag): continue
            low=" "+s.lower()+" "
            # keep strings that look English and do not already look mostly German
            score=sum(1 for x in english_markers if x in low)
            gscore=sum(1 for x in german_markers if x.lower() in low)
            # single common UI words
            singles={"About","Device","System","Never","Unknown","Active","Set active","Theme","Network","Details","Filter","Selected","Open","Cancel","Back","Close","Watch","Playlist","Name","Password","Username","Daily","Weekly","Posters","Developer","Player","Sync","General","Appearance"}
            if score>0 or s in singles:
                if gscore==0:
                    rows.append((str(p),n,s,sline))
for root in roots:
    if root.is_file(): scan_file(root)
    elif root.exists():
        for p in root.rglob("*.kt"): scan_file(p)
rows.sort()
Path("REMAINING_ENGLISH_UI.tsv").write_text("path\tline\tstring\tcontext\n"+"\n".join(
    f"{p}\t{n}\t{s.replace(chr(9),' ')}\t{c.replace(chr(9),' ')}" for p,n,s,c in rows
)+"\n",encoding="utf-8")
print("remaining",len(rows))

# rerun after de2 translation pass
