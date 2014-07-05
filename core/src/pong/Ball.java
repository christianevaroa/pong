package pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Ball {

	private Sprite sprite;
	protected Vector2 position;
	private Circle collider;
	protected float radius;
	private float direction;
	private float speed;
	private float maxSpeed;

	private float wallBounceTimer;
	private float paddleBounceTimer;
	private boolean canBounceWall;
	private boolean canBouncePaddle;
	private final float min = 0.25f;
	private final float maxXSpeed = Gdx.graphics.getWidth()*2;
	
	private final float WALL_SPEED_INCREASE = 1.01f;
	private final float PADDLE_SPEED_INCREASE = 1.02f;

	public Ball(Sprite spr, Vector2 pos){
		sprite = spr;
		float x = pos.x;
		float y = pos.y;
		position = new Vector2(x, y);
		sprite.setPosition(position.x, position.y);
		radius = sprite.getWidth()/2;
		
		maxSpeed = Gdx.graphics.getHeight()*2;

		reset(pos);

		collider = new Circle();
		collider.setRadius(sprite.getWidth()/2);
		collider.setPosition(position);
	}

	public void update(float dt) {
		// Check speed and calculate X/Y distances for this frame from direction
		if(speed > maxXSpeed){
			speed = maxXSpeed;
		}
		float distX = MathUtils.cosDeg(direction) * speed;
		float distY = MathUtils.sinDeg(direction) * speed;
		
		// Check bounce timers
		if(!canBounceWall){
			wallBounceTimer += dt;
			if(wallBounceTimer >= min){
				wallBounceTimer = 0.0f;
				canBounceWall = true;
			}
		}
		if(!canBouncePaddle){
			paddleBounceTimer += dt;
			if(paddleBounceTimer >= 0.5f){
				paddleBounceTimer = 0.0f;
				canBouncePaddle = true;
			}
		}
		position.y += distY * dt;
		position.x += distX * dt;
		sprite.setPosition(position.x-radius, position.y-radius);
		collider.setPosition(position);
	}

	public void draw(SpriteBatch batch){
		sprite.draw(batch);
	}

	public Circle getCollider(){
		return collider;
	}

	public void setPosition(Vector2 pos){
		float x = pos.x;
		float y = pos.y;
		position = new Vector2(x, y);
		sprite.setPosition(position.x-radius, position.y-radius);
	}

	/**
	 * Ball hits the side of the screen
	 */
	public void wallBounce(){
		if(canBounceWall){
			direction = 180 - direction;
			checkDirection();
			canBounceWall = false;
			wallBounceTimer = 0.0f;
			speed *= WALL_SPEED_INCREASE;
		}
	}

	public void paddleBounce(Bat paddle){
		if(canBouncePaddle){
			float diffx = paddle.position.x - this.position.x;
			float diffy = Math.min(paddle.position.y, this.position.y) - Math.max(paddle.position.y, this.position.y);
			float angle = MathUtils.atan2(diffy, diffx) * 180 / MathUtils.PI;
			if(direction < 180) {
				direction = angle;
			}
			else {
				direction = 180+angle;
			}
			checkDirection();
			canBouncePaddle = false;
			paddleBounceTimer = 0.0f;
			speed *= PADDLE_SPEED_INCREASE;
		}
	}
	
	private void checkDirection() {
		if(direction > 360) {
			direction -= 360;
		} else if (direction < 0) {
			direction += 360;
		}
	}

	/**
	 * Put the ball back at the given position (eg mid screen)
	 * Give it a random starting angle ( > 30 deg from horizontal)
	 * and reset it's speed.
	 * @param pos The starting position
	 */
	public void reset(Vector2 pos) {
		position = pos;
		direction = MathUtils.random(30, 150);
		if(MathUtils.random() < 0.5) {
			direction = -direction;
		}
		checkDirection();
		speed = Gdx.graphics.getHeight() * 0.75f;
	}

}
