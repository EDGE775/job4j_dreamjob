package ru.job4j.dream.servlet;

import ru.job4j.dream.model.User;
import ru.job4j.dream.store.PsqlStore;
import ru.job4j.dream.store.Store;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class RegServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("reg.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        if ("".equals(name) || "".equals(email) || "".equals(password)) {
            req.setAttribute("error", "Строки не могут быть пустыми!");
            req.getRequestDispatcher("reg.jsp").forward(req, resp);
        } else {
            Store store = PsqlStore.instOf();
            if (store.findUserByEmail(email) != null) {
                req.setAttribute("error", "Пользователь с такой почтой уже зарегистрирован!");
                req.getRequestDispatcher("reg.jsp").forward(req, resp);
            }
            store.saveUser(new User(0, name, email, password));
            resp.sendRedirect(req.getContextPath() + "/auth.do");
        }
    }
}

