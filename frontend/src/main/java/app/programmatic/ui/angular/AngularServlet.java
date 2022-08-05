package app.programmatic.ui.angular;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AngularServlet extends HttpServlet {
    private final String indexHtmlContents;
    private final boolean off;


    public AngularServlet() {
        AngularHelper.ContentLocation location = AngularHelper.getDevAngularIndexHtmlLocation();
        if (!location.exists()) {
            indexHtmlContents = null;
            off = true;
            return;
        }

        Resource resource = new DefaultResourceLoader().getResource(location.getPath());
        try (InputStreamReader isr = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(isr)) {
            indexHtmlContents = reader.lines().collect(Collectors.joining("\n"));
            off = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isOff() {
        return off;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (off) {
            resp.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.getWriter().print(indexHtmlContents);
        resp.setStatus(HttpStatus.OK.value());
    }
}
