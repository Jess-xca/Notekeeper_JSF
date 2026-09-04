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
        TagDAO tagDAO = new TagDAO();
        if (tagDAO.findAll().isEmpty()) {
            tagDAO.save(new Tag("Work", "#2563EB"));
            tagDAO.save(new Tag("Personal", "#059669"));
            tagDAO.save(new Tag("Study", "#D97706"));
        }

        WorkspaceDAO workspaceDAO = new WorkspaceDAO();
        if (workspaceDAO.findAll().isEmpty()) {
            workspaceDAO.save(new Workspace("Inbox", "Default landing workspace for new notes.", "📥", "Jessica Irakoze", true));
            workspaceDAO.save(new Workspace("Research", "Shared space for research notes and drafts.", "🔬", "Alain Muvunyi", false));
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HibernateUtil.shutdown();
    }
}
