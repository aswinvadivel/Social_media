# Social Media Platform - Environment Variables Setup Guide

## Using IDE Run Configuration (Easier for Development)

### IntelliJ IDEA - Step by Step:

1. **Open Run Configurations**
   - Click **Run** → **Edit Configurations** (or press `Ctrl+Alt+R`)

2. **Create New Application Configuration**
   - Click the **+** button (top-left)
   - Select **Application**

3. **Configure the Application**
   - **Name**: `Social Media Backend` (or any name)
   - **Main class**: Select your main Spring Boot class (e.g., `backend.BackendApplication`)
   - **Working directory**: Your project backend folder

4. **Add Environment Variables**
   - Find the **Environment variables** field
   - Paste this complete string (using semicolons):
   ```
   DB_PASSWORD=Joshjt;DB_HOST=localhost;DB_PORT=3306;DB_NAME=my_small_sm;DB_USERNAME=root;SERVER_PORT=8080
   ```
   - **OR** click the **...** button to add them manually one by one:
     - `DB_HOST` = `localhost`
     - `DB_PORT` = `3306`
     - `DB_NAME` = `my_small_sm`
     - `DB_USERNAME` = `root`
     - `DB_PASSWORD` = `Joshjt`
     - `SERVER_PORT` = `8080`

5. **Click OK** and then **Run** (green play button)
   - The application will use these variables automatically

### VS Code with Spring Boot:

1. **Open Debug Configuration** in `.vscode/launch.json`
2. **Add environment variables** to Spring Boot launch config
3. Same variable names as above

---

## Default Values in application.properties:

If an environment variable is NOT set, these defaults are used:

- `DB_HOST`: localhost
- `DB_PORT`: 3306
- `DB_NAME`: my_small_sm
- `DB_USERNAME`: root
- `DB_PASSWORD`: (REQUIRED - must be set)
- `SERVER_PORT`: 8080

---

## For Your Teammate (Ashwin):

If Ashwin has a **different MySQL password**, he only needs to change the `DB_PASSWORD` variable:

```cmd
setx DB_PASSWORD his_mysql_password
```

He can keep all other variables the same!

---

## pom.xml Dependency Check:

Make sure you have this MySQL dependency in your `pom.xml`:

```xml
<!-- MySQL Connector -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

Or for newer versions:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>
```

---

## Troubleshooting:

**Error: "Could not connect to database"**
- Verify MySQL is running: `mysql -u root -p`
- Check DB_PASSWORD is correct
- Verify database `my_small_sm` exists

**Error: "Access denied for user 'root'@'localhost'"**
- Check DB_USERNAME and DB_PASSWORD are correct
- Restart IDE after setting environment variables

**Error: "Unknown database 'my_small_sm'"**
- Create the database:
  ```sql
  CREATE DATABASE my_small_sm;
  ```
- Or set `spring.jpa.hibernate.ddl-auto=create` temporarily to auto-create

