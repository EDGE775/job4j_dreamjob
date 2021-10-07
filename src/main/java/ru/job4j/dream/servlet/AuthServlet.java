package ru.job4j.dream.servlet;

import ru.job4j.dream.model.User;
import ru.job4j.dream.store.PsqlStore;
import ru.job4j.dream.store.Store;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

public class AuthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("login.jsp").forward(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        Store store = PsqlStore.instOf();
        User user = store.findUserByEmail(email);
        if (user == null) {
            req.setAttribute("error", "Пользователь с такой почтой не найден!");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        } else {
            if (Objects.equals(password, user.getPassword())) {
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                resp.sendRedirect(req.getContextPath() + "/posts.do");
            } else {
                req.setAttribute("error", "Неверный пароль!");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            }
        }
    }
}