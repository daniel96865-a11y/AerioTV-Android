from pathlib import Path
import re

roots = [Path("app/src/main/java"), Path("app/src/github/java")]
lit = re.compile(r'"((?:\\.|[^"\\])*)"')
ui_hints = (
    "Text(", "text =", "title =", "label =", "subtitle =", "placeholder",
    "contentDescription", "message =", "description =", "eyebrow =", "badge =",
    "TvHeroButton(", "HeroPill(", "HeroRound(", "TvMenuAction(", "SourceTypeCard(",
    "SettingsNavRow(", "SettingsRow(", "InfoCard(", "surfaceCastFailure(",
    "Outcome.Failed(", "return fail(", "UpdateState.Error(", "notice ="
)
skip_line = ("Log.", "Logger.", "println(", "require(", "check(", "assert", "Regex(")
english_words = {
    "the","a","an","to","and","or","your","this","that","is","are","was","were",
    "no","not","from","on","off","with","for","can","cannot","could","please",
    "failed","failure","available","recording","recordings","channel","channels",
    "server","playlist","playlists","search","settings","movie","movies","show","shows",
    "play","playing","watch","sync","signed","sign","delete","add","remove","choose",
    "connection","color","start","end","hour","hours","minute","minutes","device",
    "stream","streams","audio","subtitle","subtitles","cast","casting","loading",
    "download","install","update","guide","program","programs","episode","season",
    "next","previous","clear","save","cancel","done","retry","refresh","enable",
    "disable","default","appearance","general","about","developer","remote","control",
    "continue","recent","scheduled","details","options","sort","filter","all","none",
    "live","now","today","tomorrow","jump","track","unknown","network","account",
    "permission","access","select","selecting","selected","open","close","back",
    "resume","beginning","results","ready","error","enter","name","password","username"
}
single_ui = {
    "Back","Next","Done","Cancel","Save","Delete","Remove","Edit","Add","Close","Retry",
    "Refresh","Enable","Disable","General","Appearance","Player","Developer","About",
    "Playlists","Search","Settings","Favorites","Movies","Recording","Results","Details",
    "Options","Today","Tomorrow","Now","All","None","Sort","Filter","Record","Reminder",
    "Channel","Channels","Subtitles","Audio","Multiview","Quality","Default","Language",
    "Theme","Dark","Light","System","Install","Download","Episode","Track","Clear"
}

rows=[]
for root in roots:
    if not root.exists(): continue
    for p in root.rglob("*.kt"):
        for n,line in enumerate(p.read_text(encoding="utf-8",errors="ignore").splitlines(),1):
            stripped=line.strip()
            if stripped.startswith(("//","*","/*")): continue
            if any(x in line for x in skip_line): continue
            if not any(h in line for h in ui_hints): continue
            for m in lit.finditer(line):
                s=m.group(1)
                if not re.search(r"[A-Za-z]",s): continue
                if any(x in s for x in ("http://","https://","application/","aeriotv://","aeriotvde://","/api/")): continue
                words=set(re.findall(r"[A-Za-z]+",s.lower()))
                if s in single_ui or len(words & english_words)>=1:
                    rows.append((str(p),n,s,stripped))
Path("GERMAN_UI_REMAINING.tsv").write_text(
    "path\tline\tstring\tcontext\n" + "\n".join(
        f"{p}\t{n}\t{s.replace(chr(9),' ')}\t{c.replace(chr(9),' ')}" for p,n,s,c in rows
    ) + "\n", encoding="utf-8"
)
print(f"Focused UI candidates: {len(rows)}")
