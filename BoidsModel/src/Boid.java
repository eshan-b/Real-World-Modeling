import java.awt.*;
/** std.lib for Picture is needed **/

public class Boid {
    private Bird[] boids;
    private int width, height;
    private Picture p;

    private int total, visualRange, separationDistance;
    private double separation, cohesion, alignment, velocityLim;

    public Boid(int width, int height, int total, int visualRange,
                double separation, int separationDistance, double cohesion, double alignment, double velocityLim) {
        this.total = total;
        this.width = width;
        this.height = height;

        // initialize rule parameters
        this.visualRange = visualRange;
        this.separationDistance = separationDistance;
        this.separation = separation / 1000;
        this.cohesion = cohesion / 1000;
        this.alignment = alignment / 1000;
        this.velocityLim = velocityLim;

        // initialize amount of boids
        this.boids = new Bird[total];

        // boids start in random positions
        initialize(width, height);

        this.p = new Picture(width, height);
    }

    private void initialize(int width, int height) {
        for (int i = 0; i < this.total; i++) {
            this.boids[i] = new Bird(Math.random() * width, Math.random() * height,
                                    Math.random() * 10 - 5, Math.random() * 10 - 5);
        }

        for (Bird b : this.boids) limitPos(b);
    }

    public double distance(Bird b1, Bird b2) {
        return Math.sqrt((b1.x - b2.x) * (b1.x - b2.x) +
                        (b1.y - b2.y) * (b1.y - b2.y));
    }

    public void cohesion(Bird boid) {
        int centerX = 0, centerY = 0;
        int numNeighbors = 0;

        for (Bird b : boids) {
            if (distance(boid, b) < visualRange) {
                centerX += b.x;
                centerY += b.y;
                numNeighbors += 1;
            }
        }

        if (numNeighbors > 0) {
            centerX /= numNeighbors;
            centerY /= numNeighbors;

            boid.dx += (centerX - boid.x) * this.cohesion;
            boid.dy += (centerY - boid.y) * this.cohesion;
        }
    }

    public void separation(Bird boid) {
        int moveX = 0, moveY = 0;
        for (Bird b : boids) {
            if (b != boid) {
                if (distance(boid, b) < this.separationDistance) {
                    moveX += boid.x - b.x;
                    moveY += boid.y - b.y;
                }
            }
        }

        boid.dx += moveX * this.separation;
        boid.dy += moveY * this.separation;
    }

    public void alignment(Bird boid) {
        int avgDX = 0, avgDY = 0;
        int numNeighbors = 0;

        for (Bird b : boids) {
            if (distance(boid, b) < visualRange) {
                avgDX += b.dx;
                avgDY += b.dy;
                numNeighbors += 1;
            }
        }

        if (numNeighbors > 0) {
            avgDX /= numNeighbors;
            avgDY /= numNeighbors;

            boid.dx += (avgDX - boid.dx) * this.alignment;
            boid.dy += (avgDY - boid.dy) * this.alignment;
        }
    }

    public void limitPos(Bird boid) {
        if (boid.x < 0) boid.x = width - 1;
        if (boid.x > this.width - 1) boid.x = 0;

        if (boid.y < 0) boid.y = height - 1;
        if (boid.y > this.width - 1) boid.y = 0;
    }

    public void limitVelocity(Bird boid) {
        double velocity = Math.sqrt(boid.dx * boid.dx + boid.dy * boid.dy);

        if (velocity > this.velocityLim) {
            boid.dx = (boid.dx / velocity) * this.velocityLim;
            boid.dy = (boid.dy / velocity) * this.velocityLim;
        }
    }

    public void clearCanvas() {
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                this.p.set(i, j, Color.BLACK);
            }
        }
    }

    public void display() {
        for (Bird b : this.boids) {
            cohesion(b);
            separation(b);
            alignment(b);
            limitVelocity(b);
            limitPos(b);

            this.p.set((int) b.x, (int) b.y, Color.WHITE);

            b.x += b.dx;
            b.y += b.dy;
        }

        this.p.show();
    }

    public static void main(String[] args) {
        Boid b = new Boid(500, 500, 300, 30,
                50, 20, 50, 50, 1);
        while (true) {
            b.display();
            b.clearCanvas();
        }
    }
}

class Bird {
    double x, y, dx, dy;

    public Bird(double x, double y, double dx, double dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }
}