package com.rhombus.smashit;
/**
 * TODO: 
 * ADD A COUNTDOWN
*/

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;


public class Smasher extends Activity {
	Context context = Smasher.this;
	int totalScreenX, totalScreenY;
	SmashPlayer[] smashPlayers;
	
	int gameTime = 15;
	int numberOfPlayers = 3;
	
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smasher);
		smashPlayers = new SmashPlayer[numberOfPlayers];
		
		//Detect touched area
    	Display display = getWindowManager().getDefaultDisplay();
        if(android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
        	display.getSize(size);
        	totalScreenX = size.x;
        	totalScreenY = size.y;
        } else {
        	totalScreenX = display.getWidth();
        	totalScreenY = display.getHeight();
        }
        
		startCountDown();
	}
	
	private void startCountDown() {
		final TextView tView = (TextView) findViewById(R.id.countdown);
		startGameThread();
		new Handler().postDelayed(new Runnable() {
	        public void run() {
	        	tView.setTextSize(100);
	    		tView.setText("3");
	    		new Handler().postDelayed(new Runnable() {
	    	        public void run() {
	    	    		tView.setText("2");
	    	    		new Handler().postDelayed(new Runnable() {
	    	    	        public void run() {
	    	    	    		tView.setText("1");
	    	    	    		new Handler().postDelayed(new Runnable() {
	    	    	    	        public void run() {
	    	    	    	    		tView.setText("GO!");
	    	    	    	    		((RelativeLayout) findViewById(R.id.smasherMain)).removeView(tView);
	    	    	    	    		setUpSmashPlayerListeners();
	    	    	    	        }
	    	    	    	    }, 1000);
	    	    	        }
	    	    	    }, 1000);
	    	        }
	    	    }, 1000);
	        }
	    }, 1000);
	}
	
	private void startGameThread() {
		for(int i=0; i<numberOfPlayers; i++) {
			smashPlayers[i] = new SmashPlayer(i);
		}
	}
	
	private void setUpSmashPlayerListeners() {
		for(int i=0; i<numberOfPlayers; i++) {
			smashPlayers[i].setUpListener();
		}
		new Handler().postDelayed(new Runnable() {
	        public void run() {
	        	winnerCheck();
	        }
	    }, gameTime * 1000);
	}
	
	private void winnerCheck() {
		/*TextView winningPointsView = new TextView(context, null);
		RelativeLayout rViewMain = (RelativeLayout) findViewById(R.id.smasherMain);
		rViewMain.addView(winningPointsView);
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) winningPointsView.getLayoutParams();
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		winningPointsView.setLayoutParams(layoutParams);
		winningPointsView.setTextSize(70);
		if(numberOfPlayers == 1) {
			winningPointsView.setText("SCORE: " + String.valueOf(smashPlayers[0].getPoints()));
			smashPlayers[0].destroySmasher();
		} else {
			int maxPoints=Integer.MIN_VALUE, maxSmasher=-1;
			for(int i=0; i<numberOfPlayers; i++) {
				if(smashPlayers[i].getPoints() > maxPoints) {
					maxSmasher = i;
					maxPoints = smashPlayers[i].getPoints();
				}
				smashPlayers[i].destroySmasher();
			}
			//TODO: stuff with maxSmasher and maxPoints in textView.
		}*/
		for(int i=0; i<numberOfPlayers; i++) {
			smashPlayers[i].destroySmasher();
		}
	}
	
	int BGcolor[] = {Color.GRAY, Color.RED, Color.GREEN, Color.BLUE, Color.DKGRAY, Color.LTGRAY, Color.YELLOW, Color.MAGENTA, Color.CYAN};
	
	public class SmashPlayer {
		static final int alphaModifier = 125;
		RelativeLayout smasher;
		ImageView smashBox, nextSmashBox;
		TextView pointsView;
		int points, screenX, screenY, startX, startY;
		int boxSideBound;
		int playerNumber = -1;
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams nlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		//ANALYTICS
		int totalSmashedBoxes = 0, negativeBoxes = 0, negativePoints = 0;
		int[] smashedBoxesSpread = new int[10];
		TextView currentSmashPoints = new TextView(context);
		
		public SmashPlayer(int i) {
			playerNumber = i;
			points = 0;
			setUpPlayerSmasher(i);
		}
		
		private void setUpPlayerSmasher(int i) {
			if(i == 0) {
				smasher = (RelativeLayout) findViewById(R.id.smasher1);
				pointsView = (TextView) findViewById(R.id.points1);
				smashBox = (ImageView) findViewById(R.id.smashBox1);
				nextSmashBox = (ImageView) findViewById(R.id.nextSmashBox1);
			} else if(i == 1) {
				smasher = (RelativeLayout) findViewById(R.id.smasher2);
				pointsView = (TextView) findViewById(R.id.points2);
				smashBox = (ImageView) findViewById(R.id.smashBox2);
				nextSmashBox = (ImageView) findViewById(R.id.nextSmashBox2);
			} else if(i == 2) {
				smasher = (RelativeLayout) findViewById(R.id.smasher3);
				pointsView = (TextView) findViewById(R.id.points3);
				smashBox = (ImageView) findViewById(R.id.smashBox3);
				nextSmashBox = (ImageView) findViewById(R.id.nextSmashBox3);
			} else if(i == 3) {
				smasher = (RelativeLayout) findViewById(R.id.smasher4);
				pointsView = (TextView) findViewById(R.id.points4);
				smashBox = (ImageView) findViewById(R.id.smashBox4);
				nextSmashBox = (ImageView) findViewById(R.id.nextSmashBox4);
			}
			
			
			RelativeLayout.LayoutParams smasherLP = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			if(numberOfPlayers == 1) {
				smasherLP.setMargins(0, 0, 0, 0);
			} else if(numberOfPlayers == 2) {
				switch(i) {
				case 0: smasherLP.setMargins(0, 0, 0, totalScreenY/2); break;
				case 1: smasherLP.setMargins(0, totalScreenY/2, 0, 0); break;
				}
			} else if(numberOfPlayers == 3) {
				switch(i) {
				case 0: smasherLP.setMargins(0, 0, totalScreenX/2, totalScreenY/2); break;
				case 1: smasherLP.setMargins(0, totalScreenY/2, 0, 0); break;
				case 2: smasherLP.setMargins(totalScreenX/2, 0, 0, totalScreenY/2); break;
				}
			} else if(numberOfPlayers == 4) {
				switch(i) {
				case 0: smasherLP.setMargins(0, 0, totalScreenX/2, totalScreenY/2); break;
				case 1: smasherLP.setMargins(0, totalScreenY/2, totalScreenX/2, 0); break;
				case 2: smasherLP.setMargins(totalScreenX/2, 0, 0, totalScreenY/2); break;
				case 3: smasherLP.setMargins(totalScreenX/2, totalScreenY/2, 0, 0); break;
				}
			}
			smasher.setLayoutParams(smasherLP);
			screenX = totalScreenX - (smasherLP.rightMargin + smasherLP.leftMargin);
			screenY = totalScreenY - (smasherLP.bottomMargin + smasherLP.topMargin);
			startX = smasherLP.leftMargin;
			startY = smasherLP.topMargin;
			boxSideBound = screenX < screenY ? screenX : screenY;
			smasher.addView(currentSmashPoints);
			
			randomizeSmashBox();
		}
		
		//change the color of alphaModifier
		public void setUpListener() {
			nextTap();
			smashBox.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					points+=boxSideBound/lp.height;
					pointsView.setText(String.valueOf(points));
					
					//ANALYTICS:
					totalSmashedBoxes++; 
					try {
						smashedBoxesSpread[boxSideBound/lp.height]++;
					} catch(Exception e) {
						e.printStackTrace();
					}
					nextTap();
				}
			});
			
			smasher.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					points-=lp.height/(boxSideBound/5);
					pointsView.setText(String.valueOf(points));
					
					//ANALYTICS:
					negativeBoxes--; negativePoints-=lp.height/(boxSideBound/5);
				}
			});
		}
		
		//TODO: edit this part to account for startX/Y
		int boxSide = 0, boxL = 0, boxT = 0;
		private void randomizeSmashBox() {
			boxSide = (int) ((Math.random()%0.8+0.1) * boxSideBound);
			boxL = startX + (int) (Math.random() * screenX)%(screenX - boxSide - startX);
			boxT = startY + (int) (Math.random() * screenY)%(screenY - boxSide - startY);
			
			nlp.setMargins(boxL, boxT, 0, 0);
			nlp.height = nlp.width = boxSide;
			nextSmashBox.setLayoutParams(nlp);
			nextSmashBox.setBackgroundColor(Color.BLACK);
			nextSmashBox.setAlpha(alphaModifier);
		}
		
		private void nextTap() {
			lp.setMargins(boxL, boxT, 0, 0);
			lp.height = lp.width = boxSide;
			smashBox.setLayoutParams(lp);
			smashBox.setBackgroundColor(BGcolor[(int) (Math.random() * BGcolor.length)]);
			
			//ANALYTICS:
			currentSmashPoints.setText(String.valueOf(boxSideBound/lp.height) + "/ -" + String.valueOf(lp.height/(boxSideBound/5)));
			
			randomizeSmashBox();
		}
		public void destroySmasher() { smasher.removeAllViews(); showAnalytics(); }
		public void setWinner() {}
		public int getPoints() { return points; }
		private void showAnalytics() {
			LinearLayout lView = new LinearLayout(context); lView.setOrientation(LinearLayout.VERTICAL);
			TextView tView1 = new TextView(context);
			tView1.setText("Total Smashed: " + String.valueOf(totalSmashedBoxes));
			TextView tView2 = new TextView(context);
			tView2.setText("Negative Points: " + String.valueOf(negativePoints));
			TextView tView3 = new TextView(context);
			tView3.setText("Negative Boxes: " + String.valueOf(negativeBoxes));
			
			String smashedBoxesSpreadString = "";
			for(int i=0; i<10; i++)
				smashedBoxesSpreadString += String.valueOf(smashedBoxesSpread[i]) + " ";
			TextView tView4 = new TextView(context);
			tView4.setText("Spread: " + String.valueOf(smashedBoxesSpreadString));
			
			TextView tView5 = new TextView(context);
			tView5.setText("Average points per box: " + String.valueOf((points-negativePoints)/(double)totalSmashedBoxes));
						
			lView.addView(tView1); lView.addView(tView2); lView.addView(tView3); lView.addView(tView4); lView.addView(tView5);
			smasher.addView(lView);
			
			
			TextView finalPointsView = new TextView(context, null);
			smasher.addView(finalPointsView);
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) finalPointsView.getLayoutParams();
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			finalPointsView.setLayoutParams(layoutParams);
			finalPointsView.setTextSize(50);
			finalPointsView.setText("SCORE: " + String.valueOf(getPoints()));
		}
	}
	
	private void ToastItShort(String s) { Toast.makeText(context, s, 100).show(); }
    private void ToastIt(String s) { Toast.makeText(context, s, Toast.LENGTH_SHORT).show(); }
    private void ToastItLong(String s) { Toast.makeText(context, s, Toast.LENGTH_LONG).show(); }
}
