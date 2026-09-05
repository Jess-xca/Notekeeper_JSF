from PIL import Image, ImageDraw, ImageFont
from pathlib import Path

W, H = 2000, 1280
BG = (255, 255, 255)
INK = (17, 24, 39)
MUTED = (75, 85, 99)
LINE = (100, 116, 139)
HDR = (30, 58, 95)
HDR_FG = (255, 255, 255)
BOX = (255, 255, 255)
BORDER = (148, 163, 184)
ENUM_HDR = (55, 65, 81)
ENUM_BG = (249, 250, 251)
LEGEND_BG = (248, 250, 252)

img = Image.new("RGB", (W, H), BG)
draw = ImageDraw.Draw(img)


def font(size, bold=False):
    for p in [
        r"C:\Windows\Fonts\calibrib.ttf" if bold else r"C:\Windows\Fonts\calibri.ttf",
        r"C:\Windows\Fonts\arialbd.ttf" if bold else r"C:\Windows\Fonts\arial.ttf",
    ]:
        try:
            return ImageFont.truetype(p, size)
        except Exception:
            pass
    return ImageFont.load_default()


FT = font(26, True)
FN = font(13, True)
FB = font(11)
FL = font(11)
FS = font(12)

ROW = 16
HEADER = 28


def box_h(n):
    return HEADER + 12 + n * ROW + 8


classes = {
    "User": {
        "attrs": [
            "id: String",
            "username: String",
            "email: String",
            "password: String",
            "firstName: String",
            "lastName: String",
            "role: String",
            "twoFactorEnabled: Boolean",
            "createdAt: DateTime",
        ],
        "pos": (50, 90),
        "w": 250,
    },
    "UserProfile": {
        "attrs": [
            "id: String",
            "bio: String",
            "avatarUrl: String",
            "theme: String",
            "language: String",
            "updatedAt: DateTime",
        ],
        "pos": (50, 350),
        "w": 250,
    },
    "Location": {
        "attrs": [
            "id: String",
            "name: String",
            "code: String",
            "type: LocationType",
            "createdAt: DateTime",
        ],
        "pos": (50, 550),
        "w": 250,
    },
    "Workspace": {
        "attrs": [
            "id: String",
            "name: String",
            "description: String",
            "icon: String",
            "isDefault: Boolean",
            "createdAt: DateTime",
        ],
        "pos": (430, 90),
        "w": 250,
    },
    "WorkspaceMember": {
        "attrs": ["id: String", "role: WorkspaceRole", "joinedAt: DateTime"],
        "pos": (430, 340),
        "w": 250,
    },
    "Page": {
        "attrs": [
            "id: String",
            "title: String",
            "content: String",
            "icon: String",
            "coverImage: String",
            "isFavorite: Boolean",
            "isArchived: Boolean",
            "createdAt: DateTime",
            "updatedAt: DateTime",
        ],
        "pos": (840, 90),
        "w": 260,
    },
    "PageShare": {
        "attrs": ["id: String", "permission: String", "sharedAt: DateTime"],
        "pos": (840, 390),
        "w": 260,
    },
    "Attachment": {
        "attrs": [
            "id: String",
            "fileName: String",
            "fileType: String",
            "fileSize: Long",
            "filePath: String",
            "uploadedAt: DateTime",
        ],
        "pos": (840, 560),
        "w": 260,
    },
    "Tag": {
        "attrs": ["id: String", "name: String", "color: String", "createdAt: DateTime"],
        "pos": (1200, 90),
        "w": 230,
    },
    "PageTag": {
        "attrs": ["id: String", "taggedAt: DateTime"],
        "pos": (1200, 260),
        "w": 230,
    },
    "Notification": {
        "attrs": [
            "id: String",
            "title: String",
            "message: String",
            "type: NotificationType",
            "isRead: Boolean",
            "createdAt: DateTime",
        ],
        "pos": (1200, 410),
        "w": 230,
    },
    "TwoFactorCode": {
        "attrs": ["id: String", "code: String", "expiryDate: DateTime", "used: Boolean"],
        "pos": (1540, 90),
        "w": 280,
    },
    "PasswordResetToken": {
        "attrs": ["id: String", "token: String", "expiryDate: DateTime", "used: Boolean"],
        "pos": (1540, 270),
        "w": 280,
    },
}

enums = {
    "WorkspaceRole": {
        "vals": ["OWNER", "EDITOR", "VIEWER"],
        "pos": (430, 540),
        "w": 210,
    },
    "NotificationType": {
        "vals": ["INFO", "SUCCESS", "WARNING", "ERROR", "SHARE", "WORKSPACE_INVITE"],
        "pos": (1200, 640),
        "w": 230,
    },
    "LocationType": {
        "vals": ["COUNTRY", "PROVINCE", "DISTRICT", "SECTOR", "CELL", "VILLAGE"],
        "pos": (50, 760),
        "w": 250,
    },
}

for c in classes.values():
    c["h"] = box_h(len(c["attrs"]))
for c in enums.values():
    c["h"] = box_h(len(c["vals"]))


def attach(c, side, t=0.5, inset=3):
    """Point on the box border, inset slightly so the line meets under the border."""
    x, y = c["pos"]
    w, h = c["w"], c["h"]
    t = max(0.08, min(0.92, t))
    if side == "right":
        return (x + w - inset, int(y + h * t))
    if side == "left":
        return (x + inset, int(y + h * t))
    if side == "bottom":
        return (int(x + w * t), y + h - inset)
    return (int(x + w * t), y + inset)


def link(points, width=2):
    """Draw a polyline that reaches entity borders."""
    draw.line(points, fill=LINE, width=width)


def label_at(x, y, text):
    bb = draw.textbbox((0, 0), text, font=FL)
    tw, th = bb[2] - bb[0], bb[3] - bb[1]
    draw.rectangle(
        [x - tw // 2 - 4, y - th // 2 - 2, x + tw // 2 + 4, y + th // 2 + 2],
        fill=BG,
    )
    draw.text((x - tw // 2, y - th // 2), text, font=FL, fill=MUTED)


def draw_class(name, c, is_enum=False):
    x, y = c["pos"]
    w, h = c["w"], c["h"]
    items = c.get("attrs") or c.get("vals")
    bg = ENUM_BG if is_enum else BOX
    hdr = ENUM_HDR if is_enum else HDR
    draw.rounded_rectangle([x, y, x + w, y + h], radius=6, fill=bg, outline=BORDER, width=2)
    draw.rectangle([x + 2, y + 2, x + w - 2, y + HEADER], fill=hdr)
    title = f"«enum» {name}" if is_enum else name
    draw.text((x + 10, y + 6), title, font=FN, fill=HDR_FG)
    draw.line([(x + 2, y + HEADER), (x + w - 2, y + HEADER)], fill=BORDER, width=1)
    ay = y + HEADER + 8
    prefix = "" if is_enum else "- "
    for item in items:
        draw.text((x + 12, ay), f"{prefix}{item}", font=FB, fill=INK)
        ay += ROW


# Centered title
title = "NoteKeeper Domain Class Diagram"
tb = draw.textbbox((0, 0), title, font=FT)
tw = tb[2] - tb[0]
draw.text(((W - tw) // 2, 22), title, font=FT, fill=INK)

U = classes["User"]
UP = classes["UserProfile"]
L = classes["Location"]
Wsp = classes["Workspace"]
WM = classes["WorkspaceMember"]
P = classes["Page"]
PS = classes["PageShare"]
A = classes["Attachment"]
T = classes["Tag"]
PT = classes["PageTag"]
N = classes["Notification"]
TF = classes["TwoFactorCode"]
PR = classes["PasswordResetToken"]
WR = enums["WorkspaceRole"]
NT = enums["NotificationType"]
LT = enums["LocationType"]

# --- Relationships (orthogonal, endpoints on borders) ---

# User - UserProfile
a, b = attach(U, "bottom", 0.5), attach(UP, "top", 0.5)
link([a, b])
label_at((a[0] + b[0]) // 2 + 42, (a[1] + b[1]) // 2, "1   profile   1")

# Location - User
a, b = attach(L, "top", 0.4), attach(U, "bottom", 0.28)
link([a, b])
label_at((a[0] + b[0]) // 2 - 24, (a[1] + b[1]) // 2, "1     *")

# Location parent (self)
x, y = L["pos"]
w, h = L["w"], L["h"]
link(
    [
        (x + w, y + 48),
        (x + w + 36, y + 48),
        (x + w + 36, y + h + 28),
        (x + w // 2, y + h + 28),
        (x + w // 2, y + h),
    ]
)
label_at(x + w + 12, y + h + 12, "1  parent  *")

# User - Workspace
a, b = attach(U, "right", 0.22), attach(Wsp, "left", 0.22)
link([a, b])
label_at((a[0] + b[0]) // 2, a[1] - 12, "1  owner  *")

# User - WorkspaceMember
a, b = attach(U, "right", 0.7), attach(WM, "left", 0.4)
mx = a[0] + 55
link([a, (mx, a[1]), (mx, b[1]), b])
label_at(mx + 22, (a[1] + b[1]) // 2, "1     *")

# Workspace - WorkspaceMember
a, b = attach(Wsp, "bottom", 0.5), attach(WM, "top", 0.5)
link([a, b])
label_at((a[0] + b[0]) // 2 + 52, (a[1] + b[1]) // 2, "1  members  *")

# Workspace - Page
a, b = attach(Wsp, "right", 0.35), attach(P, "left", 0.35)
link([a, b])
label_at((a[0] + b[0]) // 2, a[1] - 12, "1  pages  *")

# User - Page (author) over the top
a, b = attach(U, "top", 0.7), attach(P, "top", 0.25)
link([a, (a[0], 68), (b[0], 68), b])
label_at((a[0] + b[0]) // 2, 54, "1  author  *")

# WorkspaceMember - WorkspaceRole
a, b = attach(WM, "bottom", 0.5), attach(WR, "top", 0.5)
link([a, b])

# Page - PageShare
a, b = attach(P, "bottom", 0.3), attach(PS, "top", 0.3)
link([a, b])
label_at((a[0] + b[0]) // 2 + 30, (a[1] + b[1]) // 2, "1     *")

# Page - Attachment
a, b = attach(P, "bottom", 0.72), attach(A, "top", 0.55)
yy = PS["pos"][1] + PS["h"] + 18
link([a, (a[0], yy), (b[0], yy), b])
label_at(b[0] + 38, yy - 10, "1     *")

# Page - PageTag
a, b = attach(P, "right", 0.48), attach(PT, "left", 0.45)
link([a, b])
label_at((a[0] + b[0]) // 2, a[1] - 12, "1     *")

# Tag - PageTag
a, b = attach(T, "bottom", 0.5), attach(PT, "top", 0.5)
link([a, b])
label_at((a[0] + b[0]) // 2 + 30, (a[1] + b[1]) // 2, "1     *")

# User - PageShare (sharedWith) below members, through gap
a = attach(U, "bottom", 0.82)
b = attach(PS, "left", 0.5)
cy = WM["pos"][1] + WM["h"] + 28
gx = Wsp["pos"][0] + Wsp["w"] + (P["pos"][0] - Wsp["pos"][0] - Wsp["w"]) // 2
link([a, (a[0], cy), (gx, cy), (gx, b[1]), b])
label_at(gx + 6, cy - 12, "sharedWith")

# User - Attachment (uploadedBy)
a = attach(U, "bottom", 0.9)
b = attach(A, "left", 0.45)
cx = 330
cy = b[1]
link([a, (a[0], cy), (cx, cy), b])
label_at(cx + 8, cy - 14, "uploadedBy")

# Top lane: Page area to Notification / TwoFactorCode
a = attach(P, "top", 0.85)
b = attach(N, "top", 0.3)
link([a, (a[0], 68), (b[0], 68), b])
label_at((a[0] + b[0]) // 2, 54, "receives")

a = attach(N, "top", 0.7)
b = attach(TF, "left", 0.35)
link([(a[0], 68), (b[0], 68), b])
label_at(TF["pos"][0] - 72, 54, "verifies")

# Also connect User into that top lane so verifies/receives originate from User
a = attach(U, "top", 0.9)
link([a, (a[0], 68)])

# User - PasswordResetToken
a = attach(U, "right", 0.48)
b = attach(PR, "left", 0.4)
cx = 355
cy = 455
link([a, (cx, a[1]), (cx, cy), (b[0] - 16, cy), (b[0] - 16, b[1]), b])
label_at(PR["pos"][0] - 58, cy - 12, "resets")

# Notification - NotificationType
a, b = attach(N, "bottom", 0.5), attach(NT, "top", 0.5)
link([a, b])

# Location - LocationType
a, b = attach(L, "bottom", 0.5), attach(LT, "top", 0.5)
link([a, b])

# Draw boxes on top so borders cover line ends cleanly
for name, c in classes.items():
    draw_class(name, c, False)
for name, c in enums.items():
    draw_class(name, c, True)

# --- Scales / legend at bottom ---
ly = 1080
lh = 150
draw.rounded_rectangle([50, ly, W - 50, ly + lh], radius=8, fill=LEGEND_BG, outline=BORDER, width=1)
draw.text((70, ly + 14), "Scales", font=FN, fill=INK)

# Multiplicity scale
draw.text((70, ly + 48), "Multiplicity", font=FS, fill=MUTED)
draw.text((70, ly + 72), "1", font=FN, fill=INK)
draw.text((95, ly + 72), "= exactly one", font=FS, fill=INK)
draw.text((70, ly + 96), "*", font=FN, fill=INK)
draw.text((95, ly + 96), "= zero or more", font=FS, fill=INK)

# Association scale
draw.text((320, ly + 48), "Association", font=FS, fill=MUTED)
draw.line([(320, ly + 82), (400, ly + 82)], fill=LINE, width=2)
draw.text((415, ly + 72), "relationship between entities", font=FS, fill=INK)
draw.text((320, ly + 104), "1  role  *", font=FN, fill=INK)
draw.text((410, ly + 104), "= role name with cardinality", font=FS, fill=INK)

# Symbol scale
draw.text((780, ly + 48), "Symbols", font=FS, fill=MUTED)
draw.rectangle([780, ly + 72, 800, ly + 92], fill=BOX, outline=BORDER, width=2)
draw.text((812, ly + 74), "class / entity", font=FS, fill=INK)
draw.rectangle([780, ly + 104, 800, ly + 124], fill=ENUM_BG, outline=BORDER, width=2)
draw.text((812, ly + 106), "«enum»  enumeration type", font=FS, fill=INK)

# Attribute scale
draw.text((1180, ly + 48), "Attributes", font=FS, fill=MUTED)
draw.text((1180, ly + 74), "- name: Type", font=FN, fill=INK)
draw.text((1180, ly + 104), "private field with data type", font=FS, fill=INK)

out_dir = Path(__file__).resolve().parent / "images"
out_dir.mkdir(parents=True, exist_ok=True)
for name in ("class-diagram.png", "NoteKeeper_Class_Diagram.png"):
    path = out_dir / name
    img.save(path, "PNG")
    print("Saved", path)
