 Air Hockey 2D Game
 CS304 Computer Graphics - Final Project  
 Cairo University - Faculty of Science Department Computer Science 

---

Team Members
|    Name          |   ID  |     Role   |            Responsibility           |
|------------------|-------|---------------|----------------------------------|
|   Reem Ahmed     |2327062|    Team Leader| Game Engine, UI, Overall Project |
| Mahmoud Salah    |2328110|  AI & Physics | AI opponent, Physics engine      |
|Shahd Abdelmaksoud|2327257|Audio&Animation| Sound effects, Goal animations   |
|Mahmoud Mosad     |2327473|  Core Objects | Puck, Mallet, Hockey table       |
|Momen Mohamed     |2327263|    Utilities  | Main class, Constants, Integration |
 📁 Project Structure & Work Distribution
src/airhockey/
├── ai/ ← Mahmoud Salah (2328110)
│ ├── AI.java # AI opponent with 4 difficulty levels
│ └── AIController.java # AI behavior control
├── animation/ ← Shahd Abdelmaksoud (2327257)
│ └── GoalAnimation.java # Particle effects for goals
├── audio/ ← Shahd Abdelmaksoud (2327257)
│ └── AudioManager.java # Sound system with music & effects
├── core/ ← Mahmoud Mosad (2327473)
│ ├── HockeyTable.java # Hockey table rendering
│ ├── Mallet.java # Player/AI mallet implementation
│ └── Puck.java # Puck physics and movement
├── game/ ← Reem Ahmed (2327062)
│ ├── GameController.java # Main game logic
│ ├── GameEngine.java # Game loop & window management
│ └── GameRenderer.java # Graphics rendering (JOGL/Swing)
├── physics/ ← Mahmoud Salah (2328110)
│ └── PhysicsEngine.java # Collision detection & physics
├── introduction/ ← All Team
│ └── Introduction.java # Game introduction screen
├── utils/ ← Momen Mohamed (2327263)
│ └── Constants.java # Game constants & configuration
└── Main.java ← Momen Mohamed (2327263) - Entry point


 About the Project
2D Air Hockey game** implementing computer graphics concepts learned in CS304. The game features both **Swing rendering** and **JOGL (OpenGL) acceleration**.

 Features
- **Two Game Modes**: Single Player (vs AI) & Two Players
- **AI Difficulty**: 4 levels (Easy, Medium, Hard, Expert)
- **Graphics**: JOGL OpenGL with Swing fallback
- **Physics**: Realistic collision & momentum system
- **Audio**: Background music & sound effects
- **UI**: Settings menu, themes, score tracking

 Technologies Used
- **Java** (Core language)
- **JOGL** (OpenGL bindings - optional)
- **Swing/AWT** (UI components)
- **Git** (Version control)

---
 How to Run
### Option 1: IntelliJ IDEA
1. Open project in IntelliJ
2. Add JOGL jars from `lib/` folder to libraries
3. Run `Main.java`

### Option 2: Command Line
```bash
javac -cp "lib/*" src/airhockey/Main.java
java -cp "src;lib/*" airhockey.Main
 Controls
Player 1: WASD or Mouse

Player 2: Arrow Keys (in Two Player mode)

P/SPACE: Pause

R: Restart

M: Toggle mouse/keyboard

ESC: Menu

 CS304 Concepts Applied
Graphics rendering (2D/3D concepts)

Collision detection algorithms

Object-oriented design

Event-driven programming

Resource management

*Developed for CS304 Computer Graphics Course - Spring 2024*
Cairo University -  Faculty of Science Department Computer Science 
=======
# CS304-AirHockey-The-final-Project
Air Hockey Game for CS304 Course
