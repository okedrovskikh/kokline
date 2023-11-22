include("library", "backend", "frontend")
include("frontend:auth")
findProject(":frontend:auth")?.name = "auth"
