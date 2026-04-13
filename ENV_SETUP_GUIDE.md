# Social Media Platform - Environment Variables Setup Guide

## Using IDE Run Configuration (Easier for Development)

### IntelliJ IDEA / VS Code with Spring Boot:

1. **Open Run Configurations**
   - IntelliJ: Run → Edit Configurations
   - VS Code: Debug Configuration

2. **Set Environment Variables** in the configuration:
   ```
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=my_small_sm
   DB_USERNAME=root
   DB_PASSWORD=Joshjt
   SERVER_PORT=8080
   ```

3. **Run the application** - it will use these variables automatically

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

