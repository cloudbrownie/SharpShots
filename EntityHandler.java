import java.util.ArrayList;

public class EntityHandler {

    // instance vars -----------------------------------------------------------

    // researched java arraylists
    // store all entities for easy collision handling
    private ArrayList<Entity> entities = new ArrayList<>();

    // stores pointers to only asteroids (also exist in entities arraylist)
    // useful since asteroids have
    private ArrayList<Asteroid> asteroids = new ArrayList<>();

    // store pointers to only enemies for same reason
    private ArrayList<Enemy> enemies = new ArrayList<>();

    // "" ""
    private ArrayList<Buff> buffs = new ArrayList<>();

    // store all projectiles too
    private ArrayList<Bullet> projectiles = new ArrayList<>();

    // store player (also exists in entities arraylist)
    private Player p;

    // store vfx object
    private VFX vfx;

    // constructors ------------------------------------------------------------

    // constructor
    public EntityHandler(Player p) {
        // store pointer to player and add to entity list
        this.p = p;
        entities.add(p);
        vfx = new VFX();
    }

    // getters -----------------------------------------------------------------

    public int getNumberOfEnemies() {
        return enemies.size();
    }

    // other methods -----------------------------------------------------------

    // restart method
    public void restart(Player p) {
        entities.clear();
        this.p = p;
        entities.add(p);
        asteroids.clear();
        enemies.clear();
        buffs.clear();
        projectiles.clear();
        vfx.clear();
        this.p = p;
    }

    // explosions are all generated the same
    private void genExplosions(Entity e) {
        vfx.genExplosion(e.center(), e.getRadius() * 1.5);
    }

    // handle projectile collide
    private void resolveProjectileCollision(Bullet b, Entity e) {
        Vector mtv = b.collide(e);
        if (mtv.isNonZero() && b.tag != e.tag) {
            int sparks = StdRandom.uniform(3, 6);
            Vector sparkV = new Vector(b.vel);
            sparkV.clamp(1);
            vfx.addSparks(sparks, b.center(), sparkV, 90);

            Entity.resolveCollision(e, b, mtv);
            if (!e.isDead()) {
                e.damage(b.dmg);
            }
        }
    }

    // move and collide projectiles
    private void handleProjectiles(double dt, Rectangle bounds) {
        for (int i = 0; i < projectiles.size(); i++) {
            Bullet b1 = projectiles.get(i);

            // skip dead bullets
            if (b1.isDead()) {
                continue;
            }

            // remove the entity if it is out of bounds
            if (!bounds.contains(b1.center())) {
                b1.die();
                continue;
            }

            b1.update(dt);
            b1.homeOnTarget(entities, dt);

            // collide with player
            if (!p.isDead()) {
                resolveProjectileCollision(b1, p);
                if (p.isDead()) {
                    genExplosions(p);
                }
            }

            // collide with asteroids
            for (Asteroid a : asteroids) {
                if (a.isDead()) {
                    continue;
                }
                resolveProjectileCollision(b1, a);
                if (a.isDead() && b1.tag == Constants.PLAYER_TAG &&
                        !p.isDead()) {
                    p.increaseScore(a.getPoints());
                    genExplosions(a);
                    p.heal(a.area() * 0.05);
                }
            }

            // collide with enemies
            for (Enemy e : enemies) {
                if (e.isDead()) {
                    continue;
                }
                resolveProjectileCollision(b1, e);
                if (e.isDead() && b1.tag == Constants.PLAYER_TAG &&
                        !p.isDead()) {
                    p.increaseScore(e.getPoints());
                    genExplosions(e);
                    p.heal(e.area() * 0.25);
                }
            }

            // collide with other projectiles
            for (int j = i + 1; j < projectiles.size(); j++) {
                Bullet b2 = projectiles.get(j);

                if (b1 == b2 || b1.getTag() == b2.getTag()) {
                    continue;
                }

                if (b1.collide(b2)) {
                    b1.die();
                    b2.die();
                    int sparks = StdRandom.uniform(2, 3);
                    vfx.addSparks(sparks, b1.center(),
                                  Vector.scale(b1.vel, 0.5), 360);
                    vfx.addSparks(sparks, b2.center(),
                                  Vector.scale(b2.vel, 0.5), 360);
                    genExplosions(b1);
                    genExplosions(b2);
                }
            }
        }
    }

    // move all entities
    private void updateEntities(double dt, Rectangle bounds) {

        // handle the player stuff
        if (!p.isDead()) {
            p.handleInputs();
            p.update(dt);

            // player shoot
            if (p.hasShot()) {
                addProjectile(p.shoot());
                int sparks = StdRandom.uniform(3, 5);
                double angle = Math.toRadians(p.getRotation());
                Vector v = new Vector(0.75 * Math.cos(angle),
                                      0.75 * Math.sin(angle));
                vfx.addSparks(sparks, p.getHead(), v, 180);
            }

            // add exhaust particles if moving
            if (p.canGenExhaust()) {
                Point pc = p.center();
                double angle = Math.toRadians(p.rotation);
                pc.x -= p.getRadius() * Math.cos(angle);
                pc.y -= p.getRadius() * Math.sin(angle);
                vfx.addExhaust(pc, p.vel);
            }
        }
        else if (p.isDead() && p.canRespawn()) {
            p.respawn();
            vfx.genExplosion(p.center(), p.getRadius() * 20);
            Point pc = p.center();
            for (Entity e : entities) {
                // don't destroy buffs
                if (buffs.contains(e)) {
                    continue;
                }
                Vector dist = new Vector(pc, e.center());
                if (dist.norm() <= Constants.SCALE * 0.5) {
                    e.damage(Constants.PLAYER_EXPLOSTION_DMG);
                    dist.clamp(5);
                    e.modifyVel(dist);
                    genExplosions(e);
                }
            }
            for (Bullet b : projectiles) {
                Vector dist = new Vector(pc, b.center());
                if (dist.norm() <= Constants.SCALE * 0.5) {
                    b.damage(Constants.PLAYER_EXPLOSTION_DMG);
                    dist.clamp(5);
                    b.modifyVel(dist);
                    genExplosions(b);
                }
            }
        }

        // update enemies
        for (Enemy e : enemies) {
            if (!e.isDead()) {

                // check if enemy is out of bounds, if so kill it
                if (!bounds.contains(e.center())) {
                    e.die();
                    continue;
                }

                e.update(dt);

                // enemy shoot
                if (e.hasShot()) {
                    addProjectile(e.shoot());
                    int sparks = StdRandom.uniform(3, 5);
                    double angle = Math.toRadians(e.getRotation());
                    Vector v = new Vector(0.75 * Math.cos(angle),
                                          0.75 * Math.sin(angle));
                    vfx.addSparks(sparks, e.getHead(), v, 180);
                }

                // add exhaust particles if moving
                if (e.canGenExhaust()) {
                    Point ec = e.center();
                    double angle = Math.toRadians(e.rotation);
                    ec.x -= e.getRadius() * Math.cos(angle);
                    ec.y -= e.getRadius() * Math.sin(angle);
                    vfx.addExhaust(ec, e.vel);
                }
            }
            else {
                ArrayList<Buff> ebuffs = e.dropBuffs();
                entities.addAll(ebuffs);
                buffs.addAll(ebuffs);
                genExplosions(e);
            }
        }

        // store children from asteroids
        ArrayList<Asteroid> children = new ArrayList<>();

        // update try to spawn more asteroids
        for (Asteroid a : asteroids) {

            // check if asteroid is out of bounds, kill it if so
            if (!bounds.contains(a.center())) {
                a.die();
                continue;
            }

            a.update(dt);
            // check if asteroid is dead and will spawn more
            if (a.isDead() && a.canSpawn()) {
                ArrayList<Asteroid> nchildren = Asteroid.genChildren(a);

                // collide the children
                for (int i = 0; i < children.size() - 1; i++) {
                    Asteroid ch1 = children.get(i);
                    for (int j = 0; j < children.size(); j++) {
                        Asteroid ch2 = children.get(j);

                        Vector mtv = ch1.collide(ch2);
                        if (mtv.isNonZero()) {
                            ch1.translate(Vector.scale(mtv, 0.5));
                            ch2.translate(Vector.scale(mtv, -0.5));
                        }
                    }
                }

                children.addAll(nchildren);
            }
            if (a.isDead()) {
                ArrayList<Buff> ebuffs = a.dropBuffs();
                entities.addAll(ebuffs);
                buffs.addAll(ebuffs);
                genExplosions(a);
            }
        }
        // add all children
        entities.addAll(children);
        asteroids.addAll(children);
    }

    private boolean canObtainBuff(Entity probEntity, Entity probBuff) {
        return buffs.contains(probBuff) && !buffs.contains(probEntity) &&
                probEntity.area() >= 10;
    }

    private boolean asteroidBuffCollide(Entity a, Entity b) {
        if (asteroids.contains(a) && buffs.contains(b) && a.area() >= 10) {
            return true;
        }
        else if (asteroids.contains(b) && buffs.contains(a) && a.area() >= 10) {
            return true;
        }
        return false;
    }

    private boolean asteroidsContains(Entity a, Entity b) {
        return asteroids.contains(a) || asteroids.contains(b);
    }

    private boolean buffsContains(Entity a, Entity b) {
        return buffs.contains(a) || buffs.contains(b);
    }

    // collide all entities
    private void collideEntities() {
        entities.removeIf(e -> e.isDead() && e != p);
        buffs.removeIf(Entity::isDead);

        ArrayList<Buff> pickedUp = new ArrayList<>();

        // collide everything against each other
        for (int i = 0; i < entities.size() - 1; i++) {
            Entity a = entities.get(i);
            // skip the player if player is dead, or if a is dead
            if ((a == p && p.isDead()) || (a.isDead())) {
                continue;
            }
            for (int j = i + 1; j < entities.size(); j++) {
                Entity b = entities.get(j);
                if (b.isDead()) {
                    continue;
                }

                // collide
                Vector mtv = a.collide(b);
                if (mtv.isNonZero()) {
                    // apply buff if a or b is a pickup to non-asteroids
                    if (!pickedUp.contains(a) && canObtainBuff(b, a)) {
                        b.addBuff((Buff) a);
                        pickedUp.add((Buff) a);
                        vfx.addPulse(a.center(), 5, ((Buff) a).glow);
                        continue;
                    }
                    else if (!pickedUp.contains(b) && canObtainBuff(a, b)) {
                        a.addBuff((Buff) b);
                        pickedUp.add((Buff) b);
                        vfx.addPulse(b.center(), 5, ((Buff) b).glow);
                        continue;
                    }

                    if (asteroidsContains(a, b) && buffsContains(a, b) &&
                            !asteroidBuffCollide(a, b)) {
                        continue;
                    }

                    // resolve
                    Entity.resolveCollision(a, b, mtv);

                    // don't do anything cool to buffs
                    if (buffs.contains(a) || buffs.contains(b)) {
                        continue;
                    }

                    int sparks = StdRandom.uniform(5, 8);
                    Vector dist = new Vector(a.center(), b.center());
                    dist.scale(0.5);
                    Point contact = new Point(a.center(), dist);
                    dist.clamp(0.75);
                    vfx.addSparks(sparks, contact, dist, 360);

                    // same types can't hurt each other
                    if (a.getTag() == b.getTag()) {
                        continue;
                    }

                    // damage both entities
                    double dmgToA = b.area() / 2 * (b.vel.norm() - a.vel.norm());
                    double dmgToB = a.area() / 2 * (a.vel.norm() - b.vel.norm());

                    a.damage(Math.abs(dmgToA));
                    b.damage(Math.abs(dmgToB));
                    if (a.isDead()) {
                        genExplosions(a);
                        if (a != p) {
                            entities.addAll(a.dropBuffs());
                            buffs.addAll(a.dropBuffs());
                        }
                        if (b == p) {
                            p.increaseScore(a.getPoints());
                        }
                    }
                    else if (b.isDead()) {
                        genExplosions(b);
                        if (b != p) {
                            entities.addAll(b.dropBuffs());
                            buffs.addAll(b.dropBuffs());
                        }
                        if (a == p) {
                            p.increaseScore(b.getPoints());
                        }
                    }
                }
            }
        }

        // remove all buffs that were picked up
        entities.removeAll(pickedUp);
        buffs.removeAll(pickedUp);
    }

    // clean all arraylists
    private void cleanLists() {
        // clean entities for dead things
        entities.removeIf(e -> e.isDead() && e != p);
        asteroids.removeIf(Entity::isDead);
        enemies.removeIf(Entity::isDead);
        projectiles.removeIf(Entity::isDead);
        buffs.removeIf(Entity::isDead);
    }

    // add asteroid method
    public void addAsteroid(Asteroid a) {
        entities.add(a);
        asteroids.add(a);
    }

    // add enemy method
    public void addEnemy(Enemy e) {
        entities.add(e);
        enemies.add(e);
    }

    // add projectile method
    public void addProjectile(Bullet b) {
        projectiles.add(b);
    }

    // draw everything
    public void draw(Vector scroll, Rectangle camera) {
        // make list of all visible entities
        ArrayList<Entity> visible = new ArrayList<>();
        visible.addAll(entities);
        visible.addAll(projectiles);

        visible.removeIf(e -> {
            if (!camera.collide(e.poly.aabb) || e.isDead()) {
                return true;
            }
            return false;
        });

        // draw background stuff
        visible.forEach(e -> e.drawUnderline(scroll));

        // add all new buff particles
        visible.forEach(e -> vfx.addEffects(e.genBuffParticles()));

        // draw foreground stuff
        visible.forEach(e -> e.draw(scroll));
    }

    // update method that calls everything nicely
    public void update(double dt, double scale, Vector scroll) {
        Rectangle bounds = new Rectangle(-scale * 2, scale * 3,
                                         scale * 3, -scale * 2);
        bounds.translate(scroll.x, scroll.y);

        handleProjectiles(dt, bounds);

        updateEntities(dt, bounds);

        collideEntities();

        cleanLists();

        Rectangle camera = new Rectangle(0, scale, scale, 0);
        camera.translate(scroll.x, scroll.y);

        draw(scroll, camera);

        vfx.update(scroll, dt);
    }

    // to string method
    public String toString() {
        return "Num of Entities: " + entities.size() + "\n"
                + "Num of Asteroids: " + asteroids.size() + "\n"
                + "Num of Projectiles: " + projectiles.size() + "\n"
                + "Num of Pickups: " + buffs.size() + "\n";
    }

    public static void main(String[] args) {

        // testing the collide entities method


    }
}
