import java.util.ArrayList;

public class EntityHandler {

    // store all entities for easy collision handling
    private ArrayList<Entity> entities = new ArrayList<>();

    // stores pointers to only asteroids (also exist in entities arraylist)
    // useful since asteroids have
    private ArrayList<Asteroid> asteroids = new ArrayList<>();

    // create children sublist (avoids repetitive creation of the same list)
    private ArrayList<Asteroid> children = new ArrayList<>();

    // store all projectiles too
    private ArrayList<Bullet> projectiles = new ArrayList<>();

    // store player (also exists in entities arraylist)
    private Player p;

    // constructor
    public EntityHandler(Player p) {
        // store pointer to player and add to entity list
        this.p = p;
        entities.add(p);
    }

    // add asteroid method
    public void addAsteroid(Asteroid a) {
        entities.add(a);
        asteroids.add(a);
    }

    // add enemy method
    public void addEnemy(Enemy e) {
        entities.add(e);
    }

    // add projectile method
    public void addProjectile(Bullet b) {
        projectiles.add(b);
    }

    // move and collide projectiles
    private void handleProjectiles(double dt) {
        for (Bullet b : projectiles) {
            b.update(dt);

            // collide with entities
            for (Entity e : entities) {
                // skip friendly fire
                if (b.getTag() == e.getTag()) {
                    continue;
                }

                // collide (internally handles projectile death)
                Vector mtv = b.collide(e);
                if (mtv.isNonZero()) {
                    Entity.resolveCollision(e, b, mtv);
                    e.damage(b.dmg);
                }
            }
        }
    }

    // move all entities
    private void updateEntities(double dt) {

        // move all entities
        for (Entity e : entities) {
            e.update(dt);
        }

        // try to spawn more asteroids
        for (Asteroid a : asteroids) {
            // check if asteroid is dead and will spawn more
            if (a.isDead() && a.canSpawn()) {
                int n = StdRandom.uniform(4) + 1;
                for (int i = 0; i < n; i++) {
                    // generate values for new child
                    Vector vel = new Vector(a.vel);
                    vel.rotate(Asteroid.genSpawnAngle());
                    vel.scale(StdRandom.uniform(0.5, 1));

                    double ar = a.getRadius();
                    double nr = StdRandom.uniform() * ar * 0.5 + ar * 0.3;
                    Point c = a.centroid();
                    Asteroid na = Asteroid.genAsteroid(c.x, c.y, vel, nr);
                    children.add(na);
                }
            }
        }

        // add asteroid children
        asteroids.addAll(children);
        children.clear();
    }

    // collide all entities
    private void collideEntities() {
        // collide everything against each other
        for (int i = 0; i < entities.size() - 1; i++) {
            Entity a = entities.get(i);
            for (int j = i + 1; j < entities.size(); j++) {
                Entity b = entities.get(j);

                // collide
                Vector mtv = a.collide(b);
                if (mtv.isNonZero()) {
                    // resolve
                    Entity.resolveCollision(a, b, mtv);

                    // kill entity if a or b is an asteroid and the other isn't
                    if (asteroids.contains(a)) {
                        b.die();
                    }
                    else if (asteroids.contains(b)) {
                        a.die();
                    }
                }
            }
        }
    }

    // clean all arraylists
    private void cleanLists() {
        // clean entities for dead things
        entities.removeIf(Entity::isDead);
        asteroids.removeIf(Entity::isDead);
        projectiles.removeIf(Entity::isDead);
    }

    // draw everything
    public void draw(Vector scroll) {
        for (Entity e : entities) {
            e.draw(scroll);
        }

        for (Bullet b : projectiles) {
            b.draw(scroll);
        }
    }

    // update method that calls everything nicely
    public void update(double dt, Vector scroll) {
        updateEntities(dt);

        handleProjectiles(dt);

        collideEntities();

        cleanLists();

        draw(scroll);
    }

    public static void main(String[] args) {

    }
}
