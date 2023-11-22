include("library", "backend", "frontend")

include("frontend:auth")
findProject(":frontend:auth")?.name = "auth"

include("frontend:users")
findProject(":frontend:users")?.name = "users"
