package com.fj.buildtocloudrun.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import java.security.Principal;
import java.util.Map;

@RestController
@OpenAPIDefinition(servers = {@Server(url = "/", description = "This is the Server URL")})
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class HelloController {

    @GetMapping("/hello")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "Simple hello endpoint taking name as input")
    //@PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Map<String, String>> getHello(Principal principal) {

        return ResponseEntity.ok(Map.of("message", "Hello " + principal.getName()));
    }
}
