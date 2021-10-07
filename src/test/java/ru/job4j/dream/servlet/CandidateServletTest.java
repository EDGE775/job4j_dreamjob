package ru.job4j.dream.servlet;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.store.Store;
import ru.job4j.dream.store.PsqlStore;
import ru.job4j.dream.store.MemStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;

import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PsqlStore.class)
public class CandidateServletTest {

    @Test
    public void whenCreateCandidate() throws IOException, ServletException {
        Store store = MemStore.instOf();
        PowerMockito.mockStatic(PsqlStore.class);
        PowerMockito.when(PsqlStore.instOf()).thenReturn(store);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        PowerMockito.when(req.getParameter("id")).thenReturn("0");
        PowerMockito.when(req.getParameter("name")).thenReturn("name");
        new CandidateServlet().doPost(req, resp);
        Candidate result = new LinkedList<>(store.findAllCandidates()).getLast();
        Assert.assertThat(result.getName(), Is.is("name"));
    }

    @Test
    public void whenUpdateCandidate() throws IOException, ServletException {
        Store store = MemStore.instOf();
        Candidate candidate = new Candidate(0, "old name");
        store.saveCandidate(candidate);
        PowerMockito.mockStatic(PsqlStore.class);
        PowerMockito.when(PsqlStore.instOf()).thenReturn(store);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        PowerMockito.when(req.getParameter("id")).thenReturn(String.valueOf(candidate.getId()));
        PowerMockito.when(req.getParameter("name")).thenReturn("new name");
        new CandidateServlet().doPost(req, resp);
        Candidate result = store.findCandidateById(candidate.getId());
        Assert.assertThat(result.getName(), Is.is("new name"));
    }
}