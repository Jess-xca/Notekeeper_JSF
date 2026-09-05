from PIL import Image, ImageDraw, ImageFont
from pathlib import Path

W, H = 2000, 1220
BG = (255, 255, 255)
INK = (17, 24, 39)
MUTED = (75, 85, 99)
LINE = (148, 163, 175)
HDR = (30, 58, 95)
HDR_FG = (255, 255, 255)
BOX = (255, 255, 255)
BORDER = (148, 163, 184)
ENUM_HDR = (55, 65, 81)
ENUM_BG = (249, 250, 251)

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


FT = font(24, True)
FN = font(13, True)
FB = font(11)
FL = font(10)

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
        "pos": (40, 80),
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
        "pos": (40, 340),
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
        "pos": (40, 540),
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
        "pos": (420, 90),
        "w": 250,
    },
    "WorkspaceMember": {
        "attrs": ["id: String", "role: WorkspaceRole", "joinedAt: DateTime"],
        "pos": (420, 340),
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
        "pos": (820, 90),
        "w": 260,
    },
    "PageShare": {
        "attrs": ["id: String", "permission: String", "sharedAt: DateTime"],
        "pos": (820, 390),
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
        "pos": (820, 560),
        "w": 260,
    },
    "Tag": {
        "attrs": ["id: String", "name: String", "color: String", "createdAt: DateTime"],
        "pos": (1180, 90),
        "w": 230,
    },
    "PageTag": {
        "attrs": ["id: String", "taggedAt: DateTime"],
        "pos": (1180, 260),
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
        "pos": (1180, 410),
        "w": 230,
    },
    "TwoFactorCode": {
        "attrs": ["id: String", "code: String", "expiryDate: DateTime", "used: Boolean"],
        "pos": (1520, 90),
        "w": 280,
    },
    "PasswordResetToken": {
        "attrs": ["id: String", "token: String", "expiryDate: DateTime", "used: Boolean"],
        "pos": (1520, 270),
        "w": 280,
    },
}

enums = {
    "WorkspaceRole": {
        "vals": ["OWNER", "EDITOR", "VIEWER"],
        "pos": (420, 540),
        "w": 200,
    },
    "NotificationType": {
        "vals": ["INFO", "SUCCESS", "WARNING", "ERROR", "SHARE", "WORKSPACE_INVITE"],
        "pos": (1180, 630),
        "w": 230,
    },
    "LocationType": {
        "vals": ["COUNTRY", "PROVINCE", "DISTRICT", "SECTOR", "CELL", "VILLAGE"],
        "pos": (40, 740),
        "w": 250,
    },
}

for c in classes.values():
    c["h"] = box_h(len(c["attrs"]))
for c in enums.values():
    c["h"] = box_h(len(c["vals"]))


def side_point(c, side, t=0.5):
    x, y = c["pos"]
    w, h = c["w"], c["h"]
    t = max(0.1, min(0.9, t))
    if side == "right":
        return x + w, int(y + h * t)
    if side == "left":
        return x, int(y + h * t)
    if side == "bottom":
        return int(x + w * t), y + h
    return int(x + w * t), y


def draw_poly(pts):
    draw.line(pts, fill=LINE, width=1)


def label_at(x, y, text):
    bb = draw.textbbox((0, 0), text, font=FL)
    tw, th = bb[2] - bb[0], bb[3] - bb[1]
    draw.rectangle(
        [x - tw // 2 - 3, y - th // 2 - 1, x + tw // 2 + 3, y + th // 2 + 1],
        fill=BG,
    )
    draw.text((x - tw // 2, y - th // 2), text, font=FL, fill=MUTED)


# Title only
draw.text((40, 24), "NoteKeeper Domain Class Diagram", font=FT, fill=INK)

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

gap_x = Wsp["pos"][0] + Wsp["w"] + (P["pos"][0] - (Wsp["pos"][0] + Wsp["w"])) // 2

# User -> UserProfile
p1 = side_point(U, "bottom", 0.5)
p2 = side_point(UP, "top", 0.5)
draw_poly([p1, p2])
label_at((p1[0] + p2[0]) // 2 + 40, (p1[1] + p2[1]) // 2, "1  profile  1")

# Location -> User
p1 = side_point(L, "top", 0.35)
p2 = side_point(U, "bottom", 0.25)
draw_poly([p1, p2])
label_at((p1[0] + p2[0]) // 2 - 24, (p1[1] + p2[1]) // 2, "1  *")

# Location parent self
x, y = L["pos"]
w, h = L["w"], L["h"]
draw_poly(
    [
        (x + w, y + 45),
        (x + w + 34, y + 45),
        (x + w + 34, y + h + 26),
        (x + w // 2, y + h + 26),
        (x + w // 2, y + h),
    ]
)
label_at(x + w + 10, y + h + 10, "1 parent *")

# User -> Workspace
p1 = side_point(U, "right", 0.2)
p2 = side_point(Wsp, "left", 0.2)
draw_poly([p1, p2])
label_at((p1[0] + p2[0]) // 2, p1[1] - 10, "1  owner  *")

# User -> WorkspaceMember
p1 = side_point(U, "right", 0.72)
p2 = side_point(WM, "left", 0.4)
mx = p1[0] + 50
draw_poly([p1, (mx, p1[1]), (mx, p2[1]), p2])
label_at(mx + 20, (p1[1] + p2[1]) // 2, "1  *")

# Workspace -> WorkspaceMember
p1 = side_point(Wsp, "bottom", 0.45)
p2 = side_point(WM, "top", 0.45)
draw_poly([p1, p2])
label_at((p1[0] + p2[0]) // 2 + 50, (p1[1] + p2[1]) // 2, "1  members  *")

# Workspace -> Page
p1 = side_point(Wsp, "right", 0.35)
p2 = side_point(P, "left", 0.35)
draw_poly([p1, p2])
label_at((p1[0] + p2[0]) // 2, p1[1] - 10, "1  pages  *")

# User -> Page author (top)
p1 = side_point(U, "top", 0.65)
p2 = side_point(P, "top", 0.2)
draw_poly([p1, (p1[0], 58), (p2[0], 58), p2])
label_at((p1[0] + p2[0]) // 2, 46, "1  author  *")

# WorkspaceMember -> WorkspaceRole
p1 = side_point(WM, "bottom", 0.5)
p2 = side_point(WR, "top", 0.5)
draw_poly([p1, p2])

# Page -> PageShare
p1 = side_point(P, "bottom", 0.3)
p2 = side_point(PS, "top", 0.3)
draw_poly([p1, p2])
label_at((p1[0] + p2[0]) // 2 + 28, (p1[1] + p2[1]) // 2, "1  *")

# Page -> Attachment
p1 = side_point(P, "bottom", 0.75)
p2 = side_point(A, "top", 0.55)
yy = PS["pos"][1] + PS["h"] + 16
draw_poly([p1, (p1[0], yy), (p2[0], yy), p2])
label_at(p2[0] + 36, yy - 8, "1  *")

# Page -> PageTag
p1 = side_point(P, "right", 0.45)
p2 = side_point(PT, "left", 0.4)
draw_poly([p1, p2])
label_at((p1[0] + p2[0]) // 2, p1[1] - 10, "1  *")

# Tag -> PageTag
p1 = side_point(T, "bottom", 0.5)
p2 = side_point(PT, "top", 0.5)
draw_poly([p1, p2])
label_at((p1[0] + p2[0]) // 2 + 28, (p1[1] + p2[1]) // 2, "1  *")

# User -> PageShare sharedWith (through wide gap, below WorkspaceMember)
p1 = side_point(U, "bottom", 0.85)
p2 = side_point(PS, "left", 0.55)
cy = WM["pos"][1] + WM["h"] + 30
draw_poly([p1, (p1[0], cy), (gap_x, cy), (gap_x, p2[1]), p2])
label_at(gap_x + 4, cy - 12, "sharedWith")

# User -> Attachment uploadedBy
p1 = side_point(UP, "right", 0.75)
p2 = side_point(A, "left", 0.45)
cx = 340
cy = A["pos"][1] + A["h"] // 2
draw_poly([p1, (cx, p1[1]), (cx, cy), p2])
label_at(cx + 4, cy - 12, "uploadedBy")

# User -> Notification / TwoFactorCode along top
p2 = side_point(N, "top", 0.25)
draw_poly([(P["pos"][0] + int(P["w"] * 0.75), 58), (p2[0], 58), p2])
label_at((P["pos"][0] + N["pos"][0]) // 2 + 40, 46, "receives")

p2 = side_point(TF, "left", 0.35)
draw_poly([(N["pos"][0] + N["w"] // 2, 58), (p2[0], 58), p2])
label_at(TF["pos"][0] - 70, 46, "verifies")

# User -> PasswordResetToken
p1 = side_point(U, "right", 0.5)
p2 = side_point(PR, "left", 0.4)
cx = 350
cy = 450
draw_poly([p1, (cx, p1[1]), (cx, cy), (p2[0] - 18, cy), (p2[0] - 18, p2[1]), p2])
label_at(PR["pos"][0] - 60, cy - 12, "resets")

# Notification -> NotificationType
p1 = side_point(N, "bottom", 0.5)
p2 = side_point(NT, "top", 0.5)
draw_poly([p1, p2])

# Location -> LocationType
p1 = side_point(L, "bottom", 0.5)
p2 = side_point(LT, "top", 0.5)
draw_poly([p1, p2])


def draw_class(name, c, is_enum=False):
    x, y = c["pos"]
    w, h = c["w"], c["h"]
    items = c.get("attrs") or c.get("vals")
    bg = ENUM_BG if is_enum else BOX
    hdr = ENUM_HDR if is_enum else HDR
    draw.rounded_rectangle([x, y, x + w, y + h], radius=6, fill=bg, outline=BORDER, width=1)
    draw.rectangle([x + 1, y + 1, x + w - 1, y + HEADER], fill=hdr)
    label = f"«enum» {name}" if is_enum else name
    draw.text((x + 10, y + 6), label, font=FN, fill=HDR_FG)
    draw.line([(x + 1, y + HEADER), (x + w - 1, y + HEADER)], fill=BORDER, width=1)
    ay = y + HEADER + 8
    prefix = "" if is_enum else "- "
    for item in items:
        draw.text((x + 12, ay), f"{prefix}{item}", font=FB, fill=INK)
        ay += ROW


for name, c in classes.items():
    draw_class(name, c, False)
for name, c in enums.items():
    draw_class(name, c, True)

out_dir = Path(__file__).resolve().parent / "images"
out_dir.mkdir(parents=True, exist_ok=True)
for name in ("class-diagram.png", "NoteKeeper_Class_Diagram.png"):
    path = out_dir / name
    img.save(path, "PNG")
    print("Saved", path)
