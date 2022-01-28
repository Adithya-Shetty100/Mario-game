package com.example.mariorun;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class MarioRun extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] mario=new Texture[4];
	Texture bomb;
	Texture coin;
	Texture dizzy;
	int status=0;
	int pause=0;
	float gravity=0.25f;
	float velocity=0;
	int marioY=0;
	ArrayList<Float> coinX;
	ArrayList<Float> coinY;
	ArrayList<Rectangle> coinRect; //choose gdx.rectangle not awt one
	int coinCount=0;

	ArrayList<Float> bombX;
	ArrayList<Float> bombY;
	ArrayList<Rectangle> bombRect;
	int bombCount=0;
	Random random;
	int score=0;
	int gameState=0;
	BitmapFont font;
	
	@Override
	public void create () {
		//what has to be shown when app is opened
		batch = new SpriteBatch();
		background=new Texture("bg.png"); //img location
		mario[0]=new Texture("frame-1.png");
		mario[1]=new Texture("frame-2.png");
		mario[2]=new Texture("frame-3.png");
		mario[3]=new Texture("frame-4.png");
		bomb=new Texture("bomb.png");
		coin=new Texture("coin.png");
		dizzy=new Texture("dizzy-1.png");

		marioY=Gdx.graphics.getHeight()/2 - mario[status].getHeight()/2;
		coinX=new ArrayList<Float>();
		coinY=new ArrayList<Float>();
		bombX=new ArrayList<Float>();
		bombY=new ArrayList<Float>();
		random=new Random();

		coinRect=new ArrayList<Rectangle>();
		bombRect=new ArrayList<Rectangle>();
		font=new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10,10); //or font.getData().setScale(10); XY-scaling
		//makeCoin();

	}

	public void makeCoin(){
		Float pos=random.nextFloat()*Gdx.graphics.getHeight();
		coinY.add(pos);
		coinX.add((float)Gdx.graphics.getWidth());
	}

	public void makeBomb(){
		Float pos=random.nextFloat()*Gdx.graphics.getHeight();
		bombY.add(pos);
		bombX.add((float)Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		//to repeat some parts of game like obstacles, map forever
		//press stop button to stop rendering

		batch.begin(); //start drawing
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		//starts drawing from (0,0) on left-bottom corner
		//Gdx.graphics.getWidth() gives width of the screen game runs on

		if(gameState==0){
			//when game is created
			if(Gdx.input.justTouched()){
				velocity=0;
				gameState=1;
			}

		}else if(gameState==1){
			//when game is live
			if(Gdx.input.justTouched())
				velocity=-10;

			//BOMB
			if(bombCount<250){
				bombCount++;
			} else{
				bombCount=0;
				makeBomb();
			}
			bombRect.clear();
			for(int i=0;i<bombX.size();i++){
				batch.draw(bomb,bombX.get(i),bombY.get(i));
				//coinX.set(i,coinX.get(i)-coin.getWidth());
				bombX.set(i,bombX.get(i)-8);
				bombRect.add(new Rectangle(bombX.get(i),bombY.get(i),bomb.getWidth(),bomb.getHeight()));
			}

			//COIN
			if(coinCount<100){
				coinCount++;
			} else{
				coinCount=0;
				makeCoin();
			}
			coinRect.clear();
			for(int i=0;i<coinX.size();i++){
				batch.draw(coin,coinX.get(i),coinY.get(i));
				//coinX.set(i,coinX.get(i)-coin.getWidth());
				coinX.set(i,coinX.get(i)-5);
				coinRect.add(new Rectangle(coinX.get(i),coinY.get(i),coin.getWidth(),coin.getHeight()));
			}

			if(pause<6)
				pause++; //to cause like delay loop, and slow down rendering of each frame
			else{
				pause=0;
				if(status<3)
					status++; //to loop through frame images, each image for 10 renderings
				else
					status=0;
				//batch.draw(mario[status],Gdx.graphics.getWidth()/2 - mario[status].getWidth()/2,Gdx.graphics.getHeight()/2 - mario[status].getHeight()/2);
				//if u do above one, img blinks as for 10 ms there is nothing to draw

			}

			velocity+=gravity;
			marioY-=velocity;
			if(marioY<0)
				marioY=0;
			else if (marioY>(Gdx.graphics.getHeight()-mario[status].getHeight()))
				marioY=Gdx.graphics.getHeight()-mario[status].getHeight();

		}else if(gameState==2){
			//when game is ended
			if(Gdx.input.justTouched()){
				gameState=1;
				status=0;
				pause=0;
				velocity=0;
				coinCount=0;
				bombCount=0;
				score=0;
				coinX.clear();
				coinY.clear();
				coinRect.clear();
				bombX.clear();
				bombY.clear();
				bombRect.clear();

			}

		}


		if(gameState==2){
			batch.draw(dizzy,Gdx.graphics.getWidth()/2 - mario[status].getWidth()/2,marioY);
			for(int i=0;i<500;i++){
				//delay loop
			}
		} else
			batch.draw(mario[status],Gdx.graphics.getWidth()/2 - mario[status].getWidth()/2,marioY);
		//pictorial representation  of setting of mario co-ordinates in book
		Rectangle manRect=new Rectangle(Gdx.graphics.getWidth()/2 - mario[status].getWidth()/2,marioY,mario[status].getWidth(),mario[status].getHeight());

		//Coin
		for(int i=0;i<coinRect.size();i++){
			if(Intersector.overlaps(manRect,coinRect.get(i))){
				Gdx.app.log("Coin","Collision!!");
				score++;
				//font.draw(batch,String.valueOf(score),100,100);
				coinX.remove(i);
				coinY.remove(i);
				coinRect.remove(i);
				break;

			}
		}

		//Bomb
		for(int i=0;i<bombRect.size();i++){
			if(Intersector.overlaps(manRect,bombRect.get(i))){
				Gdx.app.log("Bomb","Collision!!");
				gameState=2;
				bombX.remove(i);
				bombY.remove(i);
				bombRect.remove(i);
				break;
			}
		}

		font.draw(batch,String.valueOf(score),80,200);
		batch.end(); //end drawing

	}
	
	@Override
	public void dispose () {
		//to end game
		batch.dispose();

	}
}
