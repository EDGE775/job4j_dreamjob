package ru.job4j.dream.servlet;

import ru.job4j.dream.store.Store;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;

public class DeleteCandidateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        Store.instOf().deleteCandidateById(Integer.valueOf(id));
        File folder = new File("c:\\images\\");
        for (File file : folder.listFiles()) {
            String filename = file.getName().split("\\.")[0];
            if (id.equals(filename)) {
                file.delete();
                break;
            }
        }
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }
}
