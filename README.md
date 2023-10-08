# Meta

**Group**: G07

**Member**:
* Tạ Đức Hoàn - 2110A05 - Leader
* Ngô Minh Hòa - 1910A04
* Vương Quang Huy - 2010A01

**Subject**: Chia sẻ nhu yếu phẩm trong 1 cộng đồng 

## Rules

### Naming Rules

***Important: All name should be in English***

#### 1. Package Names

- Use lowercase letters only.
- Use a reverse domain name as the base package name (e.g., `com.example.myapp`).
- Avoid using underscores, hyphens, or uppercase letters in package names.

Example:
```java
package com.example.myapp;
```

#### 2. Class Names
- Use CamelCase for class names.
- Start class names with an uppercase letter.
- Use descriptive and meaningful names.
- For Activity classes, append "Activity" as a suffix to the class name.
- For Fragment classes, append "{type-of-fragment}Fragment" as a suffix to the class name.
- 
Example:
```java
public class MainActivity extends AppCompatActivity {
    // ...
}
public class MainDialogFragment extends AppCompatDialogFragment {
    // ...
}

```
#### 3. Variable Names
- Use CamelCase for variable names.
- Start variable names with a lowercase letter.
- Use meaningful and descriptive names.
- Avoid single-letter variable names, except for loop counters (i, j, k).
- 
Example:
```java
int itemCount;
String userName;
```

### 4. Constant Names
- Use uppercase letters with underscores for constant names.
- Declare constants as static final.
- Use meaningful names with all uppercase letters.
Example:
```java
public static final int MAX_COUNT = 100;
```

#### 5. Method Names
- Use CamelCase for method names.
- Start method names with a lowercase letter.
- Use meaningful and descriptive names.
- Follow the JavaBeans convention for getter and setter methods.

Example:
```java
public void calculateTotalPrice() {
    // ...
}
public void setUserName(String name) {
    // ...
}
```

#### 6. Resource Names
- Use lowercase letters and underscores for resource names (e.g., activity_main.xml, ic_launcher.png).
- Follow Android resource naming conventions for layouts, drawables, and other resources.
Example:
```
res/
    layout/
        activity_main.xml
    drawable/
        ic_launcher.png
```

### Folder Structure

```
...
├── controllers
│   ├── activities
│   │   └── MainActivity.java
│   ├── adapters
│   ├── fragments
│   └── methods
└── models
    └── User.java
```

- All **activities** should be written in `./controllers/activities/`
- All **adapters** should be written in `./controllers/adapters/`
- All **fragments** should be written in `./controllers/fragments/`
- All **shared methods** should be written in `./controllers/methods/`
- All **medels** should be written in `./models/`
