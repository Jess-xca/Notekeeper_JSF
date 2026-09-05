package com.notekeeper.jsf.config;

import com.notekeeper.jsf.dao.HibernateUtil;
import com.notekeeper.jsf.dao.TagDAO;
import com.notekeeper.jsf.dao.WorkspaceDAO;
import com.notekeeper.jsf.model.Tag;
import com.notekeeper.jsf.model.Workspace;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            TagDAO tagDAO = new TagDAO();
            if (tagDAO.findAll().isEmpty()) {
                tagDAO.save(new Tag("Work", "#2563EB"));
                tagDAO.save(new Tag("Personal", "#059669"));
                tagDAO.save(new Tag("Study", "#D97706"));
            }

            WorkspaceDAO workspaceDAO = new WorkspaceDAO();
            if (workspaceDAO.findAll().isEmpty()) {
                workspaceDAO.save(new Workspace("Personal", "Your personal workspace for private notes.", "👤", "JK", true));
                workspaceDAO.save(new Workspace("Research", "Shared space for research notes and drafts.", "🔬", "Jessica Irakoze", false));
                workspaceDAO.save(new Workspace("Business", "Professional workspace for work-related notes.", "💼", "Me", false));
            }
        } catch (Exception ex) {
            System.err.println("Unable to seed demo data: " + ex.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HibernateUtil.shutdown();
    }
}
