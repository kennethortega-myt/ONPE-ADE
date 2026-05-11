package pe.gob.onpe.scebackend.security;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

@Slf4j
public class SkipPathRequestMatcher implements RequestMatcher {
    private final OrRequestMatcher skipMatchers;
    private final RequestMatcher processingMatcher;

    public SkipPathRequestMatcher(List<String> pathsToSkip, String processingPath) {
        Assert.notNull(pathsToSkip, "No hay rutas Permitidas");
        List<RequestMatcher> m = pathsToSkip.stream()
            .map(path -> new AntPathRequestMatcher(path, null, true)) // ✅ contextRelative
            .collect(Collectors.toList());
        skipMatchers = new OrRequestMatcher(m);
        processingMatcher = new AntPathRequestMatcher(processingPath, null, true); // ✅ contextRelative
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        String fullPath = request.getRequestURI();
        log.info("Matcher evaluando URI: {}", fullPath);

        if (skipMatchers.matches(request)) {
            log.info(" → Se encuentra en pathsToSkip, NO se aplicará.");
            return false;
        }

        boolean match = processingMatcher.matches(request);
        log.info(" → ¿Coincide con pattern '{}'? {}", processingMatcher, match);
        return match;
    }
}
