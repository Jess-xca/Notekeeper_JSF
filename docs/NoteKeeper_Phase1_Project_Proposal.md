# NoteKeeper — Phase-1 Project Proposal

| | |
|---|---|
| **Project** | NoteKeeper – Collaborative Note-Taking and Workspace System |
| **Author** | Jessica Irakoze |
| **GitHub** | https://github.com/Jess-xca/Notekeeper_JSF |
| **Video** | *[Add Google Vid link after recording]* |
| **Stack** | JSF, Hibernate, PostgreSQL, Maven, Jakarta Validation |

---

## 1. Abstract

NoteKeeper is a web application for organizing notes in workspaces, labeling them with tags, and sharing them securely. The full system will include accounts, pages, sharing, notifications, and administration.

**Phase-1** documents the project and delivers a working JSF + Hibernate prototype with full CRUD on two entities: **Tag** and **Workspace**. The prototype uses three validation types and three CSS types, as required.

---

## 2. Problem Statement

Notes are often scattered across paper, phone apps, documents, and chat. That leads to:

1. Poor findability — no consistent structure, tags, or workspaces  
2. Weak collaboration — sharing a whole file often gives too much or too little access  
3. Risk of loss — no clear ownership or backup process  
4. Weak security — little role control and no strong authentication  
5. No central administration for users and content  

NoteKeeper solves this by keeping notes in workspaces, organizing them with tags, and controlling access through accounts and roles.

---

## 3. Scope

### Full project (planned)

- Authentication (login, registration, password reset, Google login, 2FA)  
- Users, roles, and profiles  
- Workspaces and members  
- Pages, tags, attachments  
- Sharing, invitations, and notifications  
- Admin management  

### Phase-1 (implemented)

- This proposal document  
- Initial class model for all entities  
- JSF + Hibernate CRUD for **Tag** and **Workspace**  
- Three validation types and three CSS types  
- Public GitHub repository  
- 5–10 minute Google Vid (screen + camera)  

### Out of scope for Phase-1

- Full login screens in JSF  
- Page editor, attachments, and sharing in JSF  
- Production deployment  

*Note: In Phase-1, workspace owner is stored as a name field. In the full system, a workspace belongs to a User.*

---

## 4. AS-IS Model

Today, capturing and sharing notes is fragmented.

| Area | Current practice |
|---|---|
| Capture | Whatever tool is nearest (paper, phone, Docs, chat) |
| Organization | Folders or none; tags rarely consistent |
| Search | Manual scrolling or memory |
| Sharing | Send a copy or share an entire file |
| Security | Device lock or basic account password |
| Collaboration | Conflicting copies; unclear ownership |
| Administration | No central view of users or content |

**Flow:** Idea → choose random tool → store locally or send a file → hard to find later → time lost reconstructing information.

---

## 5. TO-BE Model

NoteKeeper provides one web system for capture, organization, and controlled sharing.

| Area | Future practice |
|---|---|
| Capture | Pages inside a workspace |
| Organization | Workspaces, tags, favorites, archive |
| Search | Search with filters |
| Sharing | Role-based page and workspace access |
| Security | Authentication, hashed passwords, 2FA |
| Collaboration | One shared record instead of file copies |
| Administration | Admin screens for users and content |

**Phase-1 slice:** Dashboard → Tag CRUD / Workspace CRUD / Search → PostgreSQL (Hibernate) → JSF, Bean, and custom validation.

---

## 6. Business Requirements

### Full system (selected)

| ID | Requirement |
|---|---|
| BR-01 | Users can register, log in, reset passwords, and enable 2FA |
| BR-02 | Users can create and manage workspaces |
| BR-03 | Users can create, edit, favorite, archive, and delete pages |
| BR-04 | Users can create tags and assign them to pages |
| BR-05 | Workspace owners can invite members and assign roles |
| BR-06 | Page owners can share pages with view or edit permission |
| BR-07 | Attachments can be linked to pages |
| BR-08 | Users receive notifications for shares and invitations |
| BR-09 | Admins can manage users, pages, and workspaces |
| BR-10 | Invalid data is rejected by validation before save |

### Phase-1 (implemented)

| ID | Requirement | Status |
|---|---|---|
| P1-01 | CRUD for tags | Done |
| P1-02 | CRUD for workspaces | Done |
| P1-03 | Validate tag name and color | Done |
| P1-04 | Validate workspace name, owner, and description | Done |
| P1-05 | Persist data with Hibernate (`hibernate.cfg.xml`) | Done |
| P1-06 | Dashboard with live counts | Done |
| P1-07 | Search across tags and workspaces | Done |
| P1-08 | One default workspace (prefer **Personal**) | Done |

---

## 7. Software Qualities

| Quality | Application |
|---|---|
| Functionality | CRUD for Tag and Workspace; full domain planned |
| Usability | Clear forms, messages, and confirm-before-delete |
| Reliability | Transactions with rollback; validation before persist |
| Security | Full system: auth, hashing, 2FA, roles; Phase-1: input validation |
| Maintainability | Layers: model, DAO, bean, validator, view |
| Portability | Maven WAR on Tomcat 10; PostgreSQL |
| Integrity | Unique names, required fields, reserved workspace names |
| Reusability | Shared CSS, DAOs, and validators |
| Testability | DAO and validators separated from the view |
| Adaptability | Same domain aligns with the Spring Boot backend |

---

## 8. Initial Class Diagram (Entities)

Main entities in the full design:

**User**, **UserProfile**, **Location**, **Workspace**, **WorkspaceMember**, **Page**, **Tag**, **PageTag**, **PageShare**, **Attachment**, **Notification**, **TwoFactorCode**, **PasswordResetToken**

Key relationships:

- User owns Workspaces and authors Pages  
- Workspace contains Pages and WorkspaceMembers  
- Page links to Tags (via PageTag), Shares, and Attachments  
- User receives Notifications and uses 2FA / password-reset tokens  

### Phase-1 entities (implemented)

**Tag:** id, name, color, createdAt  

**Workspace:** id, name, description, icon, ownerName, isDefault, createdAt  

*(In the full system, Workspace.owner is a User reference instead of ownerName.)*

---

## 9. Phase-1 Implementation

### Selected entities

| Entity | Reason |
|---|---|
| **Tag** | Clear rules for uniqueness and color format |
| **Workspace** | Core product concept; required fields and custom name rules |

### CRUD

| Operation | Tag | Workspace |
|---|---|---|
| Create | New tag form | New workspace form |
| Read | Tags table | Workspaces table |
| Update | Edit then save | Edit then save |
| Delete | Confirm then delete | Confirm then delete |

### Three validation types

| Type | Examples |
|---|---|
| JSF built-in | `required`, `f:validateLength`, `f:validateRegex` |
| Bean Validation | `@NotBlank`, `@Size`, `@Pattern` on entities |
| Custom | UniqueTagNameValidator, HexColorValidator, WorkspaceNameValidator |

### Three CSS types

| Type | Location |
|---|---|
| External | `resources/css/app.css` |
| Internal | `<style>` in `tags.xhtml` and `workspaces.xhtml` |
| Inline | Color swatches and selected headings |

### Default workspace

Only one default workspace is shown. A workspace named **Personal** is preferred. The badge uses `h:panelGroup` with `rendered="#{item.isDefault}"`.

### How to run

Requirements: JDK 17, Maven, PostgreSQL (`notekeeper_db`). Set the password in `hibernate.cfg.xml`.

```
mvn clean package cargo:run
```

- Dashboard: http://localhost:8081/notekeeper-jsf/  
- Tags: http://localhost:8081/notekeeper-jsf/tags.xhtml  
- Workspaces: http://localhost:8081/notekeeper-jsf/workspaces.xhtml  

---

## 10. Public GitHub Link

https://github.com/Jess-xca/Notekeeper_JSF  

The repository should remain **public** for assessment.

---

## 11. Video (Google Vid)

| Item | Detail |
|---|---|
| Duration | 5–10 minutes |
| Format | Screen + camera |
| Tool | Google Vid |
| Link | *[Paste link here]* |

**Suggested outline**

1. Introduce yourself and the problem (≈1 min)  
2. Walk through this proposal (≈2 min)  
3. Demo Tag and Workspace CRUD, validation, CSS, and search (≈5 min)  
4. Show GitHub and close (≈1–2 min)  

---

## 12. Conclusion

Phase-1 defines NoteKeeper and proves the approach with a JSF and Hibernate CRUD prototype for Tag and Workspace. Later phases will add authentication, pages, sharing, and administration to complete the product.
