package ru.job4j.dream.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import ru.job4j.dream.store.Store;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoUploadServlet extends HttpServlet {
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        File folder = new File("c:\\images\\");
        deleteCandidateImage(id, folder);
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.valueOf(req.getParameter("id"));
        req.setAttribute("candidate", Store.instOf().findCandidateById(id));
        RequestDispatcher dispatcher = req.getRequestDispatcher("/photoupload.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("delete".equals(action)) {
            doDelete(req, resp);
        } else {
            String id = req.getParameter("id");
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletContext servletContext = this.getServletConfig().getServletContext();
            File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
            factory.setRepository(repository);
            ServletFileUpload upload = new ServletFileUpload(factory);
            try {
                List<FileItem> items = upload.parseRequest(req);
                File folder = new File("c:\\images\\");
                if (!folder.exists()) {
                    folder.mkdir();
                }
                for (FileItem item : items) {
                    if (!item.isFormField()) {
                        if ("".equals(item.getName())) {
                            break;
                        }
                        deleteCandidateImage(id, folder);
                        String[] fileNameArray = item.getName().split("\\.");
                        String candidateFileName = id.concat(".").concat(fileNameArray[fileNameArray.length - 1]);
                        File file = new File(folder + File.separator + candidateFileName);
                        try (FileOutputStream out = new FileOutputStream(file)) {
                            out.write(item.getInputStream().readAllBytes());
                        }
                    }
                    break;
                }
            } catch (FileUploadException e) {
                e.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/candidates.do");
        }
    }

    private void deleteCandidateImage(String id, File folder) {
        for (File file : folder.listFiles()) {
            String filename = file.getName().split("\\.")[0];
            if (id.equals(filename)) {
                file.delete();
                break;
            }
        }
    }
}
