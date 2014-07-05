package pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bat {

	Sprite sprite;
	Vector2 position;
	Vector2 velocity;
	float speed = Gdx.graphics.getWidth();
	float width;
	float height;
	Rectangle collider;
	
	public Bat(Sprite spr, Vector2 pos){
		sprite = spr;
		float x = pos.x;
		float y = pos.y;
		position = new Vector2(x, y);
		sprite.setPosition(position.x, position.y);
		velocity = new Vector2(0,0);
		width = sprite.getWidth();
		height = sprite.getHeight();
		
		collider = new Rectangle();
		collider.setWidth(width);
		collider.setHeight(height);
		collider.setCenter(position);
	}
	
	public void update(float dt) {
		sprite.setPosition(position.x-(width/2), position.y-(height/2));
		collider.setCenter(position);
	}
	
	public void draw(SpriteBatch batch){
		sprite.draw(batch);
	}
	
	public Rectangle getCollider() {
		return collider;
	}
	
	public void setPosition(Vector2 pos){
		float x = pos.x;
		float y = pos.y;
		position = new Vector2(x, y);
		sprite.setPosition(position.x-(width/2), position.y-(height/2));
	}
	
	public float getCenter() {
		return this.position.x + (width/2);
	}
	
}
