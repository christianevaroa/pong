package pong;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

public class GDXGame extends ApplicationAdapter {

	SpriteBatch batch;
	TextureAtlas atlas;
	BitmapFont font;
	Bat player;
	Bat computer;
	Ball ball;
	Vector2 initialBallPos;
	Vector2 initialPlayerPos;
	Vector2 initialComputerPos;

	ShapeRenderer sr;

	boolean debug = false;
	boolean paused = true;
	float timer = 0.0f;
	float totalTime = 0.0f;
	
	int playerScore;
	int computerScore;

	@Override
	public void create () {
		font = new BitmapFont();
		batch = new SpriteBatch();
		atlas = new TextureAtlas(Gdx.files.internal("pong.pack"));
		Sprite ballSprite = new Sprite(atlas.findRegion("ball"));
		initialBallPos = new Vector2(Gdx.graphics.getWidth()/2.0f, Gdx.graphics.getHeight()/2.0f);
		ball = new Ball(ballSprite, initialBallPos);

		Sprite batSprite = new Sprite(atlas.findRegion("bat"));
		initialPlayerPos = new Vector2(Gdx.graphics.getWidth()/2.0f, 10.0f);
		player = new Bat(batSprite, initialPlayerPos);

		Sprite compBatSprite = new Sprite(atlas.findRegion("bat"));
		initialComputerPos = new Vector2(Gdx.graphics.getWidth()/2.0f, Gdx.graphics.getHeight()-10.0f);
		computer = new Bat(compBatSprite, initialComputerPos);

		sr = new ShapeRenderer();

		reset();
	}

	@Override
	public void render () {
		float dt = Gdx.graphics.getDeltaTime();
		totalTime += dt;
		
		Gdx.gl.glClearColor(MathUtils.sinDeg(totalTime*10), 1, 1, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(Gdx.input.isKeyPressed(Input.Keys.F1)){
			debug = !debug;
		}

		if(paused){
			timer += dt;
			if(timer > 1){
				timer = 0;
				paused = false;
			}
		} else {

			float compMoveAmt = computer.speed * dt;
			float playerMoveAmt = player.speed * dt;

			// Move player bat in held direction
			if(Gdx.input.isKeyPressed(Input.Keys.A)){
				float npos = player.position.x - playerMoveAmt;
				if(npos - player.sprite.getWidth()/2 > 0){
					player.position.x = npos;
				}
			} else if(Gdx.input.isKeyPressed(Input.Keys.D)){
				float npos = player.position.x + playerMoveAmt;
				if(npos + player.sprite.getWidth()/2 < Gdx.graphics.getWidth()){
					player.position.x = npos;
				}
			}
			
			if(Gdx.input.isKeyPressed(Input.Keys.TAB)){
				reset();
			}

			// Move computer bat towards ball
			if(computer.position.x < ball.position.x){
				float npos = ball.position.x;
				if(compMoveAmt < Math.abs(computer.position.x - ball.position.x)){
					npos = computer.position.x + compMoveAmt;
				}
				computer.position.x = npos;

			}
			if(computer.position.x > ball.position.x){
				float npos = ball.position.x;
				if(compMoveAmt < Math.abs(computer.position.x - ball.position.x)){
					npos = computer.position.x - compMoveAmt;
				}
				computer.position.x = npos;
			}

			checkBall();
			// Update all the things
			ball.update(dt);
			player.update(dt);
			computer.update(dt);
		}
		// Draw all the things
		draw();
	}

	public void reset() {
		ball.reset(initialBallPos);
		ball.setPosition(initialBallPos);
		player.setPosition(initialPlayerPos);
		computer.setPosition(initialComputerPos);
	}

	public void draw() {
		batch.begin();
		ball.draw(batch);
		player.draw(batch);
		computer.draw(batch);
		if(paused){
			font.draw(batch, "player: "+playerScore+", computer: "+computerScore, Gdx.graphics.getWidth()/2.0f, Gdx.graphics.getHeight()/2.0f);
		}
		batch.end();

		if(debug){
			//Draw colliders
			sr.setColor(0, 1, 0, 1);
			sr.begin(ShapeRenderer.ShapeType.Filled);
			sr.circle(ball.getCollider().x, ball.getCollider().y, ball.getCollider().radius);
			sr.rect(player.getCollider().x, player.getCollider().y, player.getCollider().width, player.getCollider().height);
			sr.rect(computer.getCollider().x, computer.getCollider().y, computer.getCollider().width, computer.getCollider().height);
			sr.end();
		}
	}

	public void checkBall() {
		// Ball collides with bat
		if(Intersector.overlaps(ball.getCollider(), player.getCollider()) && ball.position.y > player.position.y){
			ball.paddleBounce(player);
		}
		else if(Intersector.overlaps(ball.getCollider(), computer.getCollider()) && ball.position.y < computer.position.y){
			ball.paddleBounce(computer);
		}

		// Ball hits edge
		if(ball.position.x < ball.radius || ball.position.x > Gdx.graphics.getWidth() - (ball.radius)) {
			ball.wallBounce();
		}

		// Ball leaves screen
		if(ball.position.y < 0 - ball.radius*2){
			computerScore++;
			reset();
			paused = true;
		} else if(ball.position.y > Gdx.graphics.getHeight() + ball.radius*2){
			playerScore++;
			reset();
			paused = true;
		}
	}
}
