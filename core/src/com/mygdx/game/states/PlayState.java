package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.FlappyBird;
import com.mygdx.game.sprites.Bird;
import com.mygdx.game.sprites.Tube;



public class PlayState extends State{

    private static final int TUBE_SPACING = 180;
    private static final int TUBE_COUNT = 4;
    private Bird bird;
    private Texture bg;
    private Tube tube;
    private Texture ground;
    private Vector2 groundPos1, groundPos2;
    private static final int GROUND_Y_OFFSET = -15;

    private Array<Tube> tubes;

    protected PlayState(GameStateManager gsm) {
        super(gsm);
        bird = new Bird(50, 300);
        bg = new Texture("bg.png");
        ground = new Texture("ground.png");
        groundPos1 = new Vector2(cam.position.x - cam.viewportWidth / 2, GROUND_Y_OFFSET);
        groundPos2 = new Vector2((cam.position.x - cam.viewportWidth / 2) + ground.getWidth(), GROUND_Y_OFFSET);
        tube = new Tube(100f);
        tubes = new Array<>();
        cam.setToOrtho(false, (float) FlappyBird.WIDTH / 2, (float) FlappyBird.HEIGHT /2);

        for (int i = 0; i <= TUBE_COUNT; i++) {
            tubes.add(new Tube(i * TUBE_SPACING + Tube.TUBE_WIDTH));
        }

    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched()) {
            bird.jump();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        updateGround();
        bird.update(dt);

        cam.position.x = bird.getPosition().x + 80;
        for (int i=0; i< tubes.size; i++) {
            Tube tube = tubes.get(i);
            if (cam.position.x - (cam.viewportWidth/2) > tube.getPosTopTube().x + tube.getTopTube().getWidth()){
                tube.rePosition(tube.getPosTopTube().x + ((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));
            }
            if (tube.collides(bird.getBounds())) {
                gsm.set(new PlayState(gsm));
            }
        }
        if (bird.getPosition().y <= ground.getHeight() + GROUND_Y_OFFSET) {
            gsm.set(new PlayState(gsm));
        }
        cam.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, cam.position.x - (cam.viewportWidth/2), 0);
        sb.draw(bird.getTexture(), bird.getPosition().x, bird.getPosition().y);
        for (Tube tube: tubes) {
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
            sb.draw(tube.getBottomTube(), tube.getPosBotTube().x, tube.getPosBotTube().y);
        }
        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);

        sb.end();

    }

    @Override
    public void dispose() {
        bg.dispose();
        bird.dispose();
        ground.dispose();
        for (Tube tube: tubes) {
            tube.dispose();
        }
    }

    private void updateGround() {
        if (cam.position.x - (cam.viewportWidth/2) > groundPos1.x + ground.getWidth()) {
            groundPos1.add(ground.getWidth() *2, 0);
        }
        if (cam.position.x - (cam.viewportWidth / 2) > groundPos2.x + ground.getWidth()) {
            groundPos2.add(ground.getWidth() *2, 0);
        }
    }
}
