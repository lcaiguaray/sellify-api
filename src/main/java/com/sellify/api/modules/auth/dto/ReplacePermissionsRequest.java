package com.sellify.api.modules.auth.dto;

import java.util.Set;

public record ReplacePermissionsRequest(
    Set<String> permissionIds
) {}