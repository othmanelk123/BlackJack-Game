import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.util.*;

public class GUI extends JFrame {
	
	//randomizer for cards
	Random rand = new Random();
	
	//temporary integer used for used status
	int tempC;
	
	//boolean that indicates whether the dealer is thinking or not
	boolean dHitter = false;
	
	//list of cards stocker une collection de cartes de jeu
	ArrayList<Card> Cards = new ArrayList<Card>();
	
	//list of messages
	ArrayList<Message> Log = new ArrayList<Message>();
	
	//fonts used
	Font fontCard = new Font("Times New Roman", Font.PLAIN, 40);
	Font fontQuest = new Font("Times New Roman", Font.BOLD, 40);
	Font fontButton = new Font("Times New Roman", Font.PLAIN, 25);
	Font fontLog = new Font("Times New Roman", Font.ITALIC, 30);
	
	//Log message colors
	Color cDealer = Color.red;
	Color cPlayer = new Color(25,55,255);
	
	//strings used
	String questHitStay = new String("Hit or Stay?");
	String questPlayMore = new String("Play more?");
	
	//colors used
	Color colorBackground = new Color(39,119,20);
	Color colorButton = new Color(204,204,0);
	
	//buttons used
	JButton bHit = new JButton();
	JButton bStay = new JButton();
	JButton bYes = new JButton();
	JButton bNo = new JButton();
	
	//screen resolution
	int sW = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	int sH = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	//window resolution
	int aW = 1366;
	int aH = 768;
	
	//card grid position and dimensions
	int gridX = 50;
	int gridY = 50;
	int gridW = 900;
	int gridH = 400;
	
	//card spacing and dimensions
	int spacing = 10;
	int rounding = 10;
	int tCardW = (int) gridW/6;
	int tCardH = (int) gridH/2;
	int cardW = tCardW - spacing*2;
	int cardH = tCardH - spacing*2;
	
	//booleans about phases
	boolean hit_stay_q = true;
	boolean dealer_turn = false;
	boolean play_more_q = false;
	
	//player and dealer card array
	ArrayList<Card> pCards = new ArrayList<Card>();
	ArrayList<Card> dCards = new ArrayList<Card>();
	
	//player and dealer totals
	int pMinTotal = 0;
	int pMaxTotal = 0;
	int dMinTotal = 0;
	int dMaxTotal = 0;
	
	//polygons for diamond shapes
	int[] polyX = new int[4];
	int[] polyY = new int[4];
	
	public GUI() { 
		this.setTitle("Blackjack");
		this.setBounds((sW-aW-6)/2, (sH-aH-29)/2, aW+6, aH+29);
		this.setResizable(true);  // redimentionner la fenetre 
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //Définit l'action à effectuer lorsque l'utilisateur ferme la fenêtre. Dans ce cas, l'application se termine lorsque la fenêtre est fermée.
		this.setVisible(true); //affichage
		
		Board board = new Board(); //un objet "Board" est créé et défini comme le contenu principal de la fenêtre
		this.setContentPane(board);
		board.setLayout(null);
		
		Move move = new Move(); //"Move" gère les événements de mouvement de souris et "Click" gère les événements de clic de souris.
		this.addMouseMotionListener(move);
		
		Click click = new Click();
		this.addMouseListener(click);
		
		//button stuff
		
		ActHit actHit = new ActHit(); //Le code que vous avez fourni crée un bouton "HIT" et lui ajoute un écouteur d'événements d'action appelé "ActHit". Il est ensuite ajouté au conteneur "board" et positionné à (1000, 200) avec une taille de 100 x 50 pixels. Le fond du bouton est défini avec une couleur "colorButton" et la police de caractères utilisée pour le texte est "fontButton".
		bHit.addActionListener(actHit);
		bHit.setBounds(1000, 200, 100, 50);
		bHit.setBackground(colorButton);
		bHit.setFont(fontButton);
		bHit.setText("HIT");
		board.add(bHit);
		
		ActStay actStay = new ActStay();
		bStay.addActionListener(actStay);
		bStay.setBounds(1150, 200, 100, 50);
		bStay.setBackground(colorButton);
		bStay.setFont(fontButton);
		bStay.setText("STAY");
		board.add(bStay);
		
		ActYes actYes = new ActYes();
		bYes.addActionListener(actYes);
		bYes.setBounds(1000, 600, 100, 50);
		bYes.setBackground(colorButton);
		bYes.setFont(fontButton);
		bYes.setText("YES");
		board.add(bYes);
		
		ActNo actNo = new ActNo();
		bNo.addActionListener(actNo);
		bNo.setBounds(1150, 600, 100, 50);
		bNo.setBackground(colorButton);
		bNo.setFont(fontButton);
		bNo.setText("NO");
		board.add(bNo);
		
		//creating all cards
		
		String temp_str = "starting_temp_str_name";
		for (int i = 0; i < 52; i++) {
			if (i % 4 == 0) {
				temp_str = "Spades";
			} else if (i % 4 == 1) {
				temp_str = "Hearts";
			} else if (i % 4 == 2) {
				temp_str = "Diamonds";
			} else if (i % 4 == 3) {
				temp_str = "Clubs";
			}
			Cards.add(new Card((i/4) + 1, temp_str, i)); //Le rang de la carte est déterminé en divisant l'index de la carte par 4 et en ajoutant 1, car il y a 13 rangs dans un jeu de cartes (2 à 10, valet, dame, roi et as). L'opération "(i/4) + 1" retourne un nombre entier compris entre 1 et 13, correspondant au rang de la carte.
		}
		/*
		System.out.println("---ooo---ooo---ooo---");
		System.out.println("Creating cards finished!");
		System.out.println("---ooo---ooo---ooo---");
		*/
		//randomly selecting initial cards for player and dealer
//Le code que vous avez fourni tire au hasard quatre cartes à partir de la liste de cartes "Cards" et les ajoute à deux listes distinctes, "pCards" pour les cartes du joueur et "dCards" pour les cartes du croupier. Il utilise la méthode "nextInt" de la classe "Random" pour générer un entier aléatoire compris entre 0 et 51, inclus.

//La première carte est ajoutée à "pCards", la deuxième à "dCards", la troisième à nouveau à "pCards" et enfin la quatrième à "dCards". Après l'ajout de chaque carte à une liste, la carte correspondante est marquée comme utilisée en appelant la méthode "setUsed" de la classe "Card". Cette méthode définit le champ "used" de l'objet "Card" à "true", ce qui permet d'indiquer que la carte ne doit plus être utilisée pour le reste de la partie.

//Le code inclut également une boucle "while" qui garantit que les cartes tirées au hasard ne sont pas déjà utilisées, afin d'éviter de tirer la même carte deux fois. La boucle continue de générer un nouvel entier aléatoire jusqu'à ce qu'elle trouve une carte qui n'a pas encore été utilisée.
		
		tempC = rand.nextInt(52);
		pCards.add(Cards.get(tempC));
		Cards.get(tempC).setUsed();
	//	System.out.println("Card " + pCards.get(0).name + " of " + pCards.get(0).shape + " added to the player's cards.");
		
		tempC = rand.nextInt(52);
		while (Cards.get(tempC).used == true) {
			tempC = rand.nextInt(52);
		}
		dCards.add(Cards.get(tempC));
		Cards.get(tempC).setUsed();
	//	System.out.println("Card " + dCards.get(0).name + " of " + dCards.get(0).shape + " added to the dealer's cards.");
		
		tempC = rand.nextInt(52);
		while (Cards.get(tempC).used == true) {
			tempC = rand.nextInt(52);
		}
		pCards.add(Cards.get(tempC));
		Cards.get(tempC).setUsed();
	//	System.out.println("Card " + pCards.get(1).name + " of " + pCards.get(1).shape + " added to the player's cards.");
		
		tempC = rand.nextInt(52);
		while (Cards.get(tempC).used == true) {
			tempC = rand.nextInt(52);
		}
		dCards.add(Cards.get(tempC));
		Cards.get(tempC).setUsed();
	//	System.out.println("Card " + dCards.get(1).name + " of " + dCards.get(1).shape + " added to the dealer's cards.");
		/*
		System.out.println("---ooo---ooo---ooo---");
		System.out.println("Setting cards finished!");
		System.out.println("---ooo---ooo---ooo---");
		*/
	}
//Cette méthode, totalsChecker(), calcule les valeurs totales minimales et maximales des cartes du joueur et du croupier dans une partie de blackjack.

//Tout d'abord, elle initialise les valeurs minimales et maximales des totaux pour les cartes du joueur et du croupier à zéro, ainsi qu'un compteur pour le nombre d'As présents dans les mains des joueurs.

//Ensuite, elle parcourt chaque carte dans la main du joueur et du croupier et ajoute la valeur de chaque carte à la fois au total minimum et maximum pour cette main. Si une carte est un As, elle incrémente le compteur d'As.

//Enfin, si le compteur d'As est supérieur à zéro, cela signifie qu'il y a au moins un As dans la main et que sa valeur peut être considérée comme 11 au lieu de 1 pour le calcul du total maximum. La méthode met donc à jour le total maximum pour prendre en compte cette possibilité.
	
	public void totalsChecker() {
		
		int acesCount;
		
		//calculation of player's totals
		pMinTotal = 0;
		pMaxTotal = 0;
		acesCount = 0;
		
		for (Card c : pCards) {
			pMinTotal += c.value;
			pMaxTotal += c.value;
			if (c.name == "Ace")
				acesCount++;
			
		}
		
		if (acesCount > 0)
			pMaxTotal += 10;
		
		dMinTotal = 0;
		dMaxTotal = 0;
		acesCount = 0;
		
		for (Card c : dCards) {
			dMinTotal += c.value;
			dMaxTotal += c.value;
			if (c.name == "Ace")
				acesCount++;
			
		}
		
		if (acesCount > 0)
			dMaxTotal += 10;
	}
	
	public void setWinner() {
		int pPoints = 0;
		int dPoints = 0;
		
		if (pMaxTotal > 21) {
			pPoints = pMinTotal;
		} else {
			pPoints = pMaxTotal;
		}
		
		if (dMaxTotal > 21) {
			dPoints = dMinTotal;
		} else {
			dPoints = dMaxTotal;
		}
		
		if (pPoints > 21 && dPoints > 21) {
			Log.add(new Message("Nobody wins!", "Dealer"));
		} else if (dPoints > 21) {
			Log.add(new Message("You win!", "Player"));
			Main.pWins++;
		} else if (pPoints > 21) {
			Log.add(new Message("Dealer wins!", "Dealer"));
			Main.dWins++;
		} else if (pPoints > dPoints) {
			Log.add(new Message("You win!", "Player"));
			Main.pWins++;
		} else {
			Log.add(new Message("Dealer wins!", "Dealer"));
			Main.dWins++;
		}
		
	}
	
	public void dealerHitStay() {
		dHitter = true;
		
		int dAvailable = 0;
		if (dMaxTotal > 21) {
			dAvailable = dMinTotal;
		} else {
			dAvailable = dMaxTotal;
		}
		
		int pAvailable = 0;
		if (pMaxTotal > 21) {
			pAvailable = pMinTotal;
		} else {
			pAvailable = pMaxTotal;
		}
		
		repaint();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if ((dAvailable < pAvailable && pAvailable <= 21) || dAvailable < 16) {
			int tempMax = 0;
			if (dMaxTotal <= 21) {
				tempMax = dMaxTotal;
			} else {
				tempMax = dMinTotal;
			}
			String mess = ("Dealer decided to hit! (total: " + Integer.toString(tempMax) + ")");
			Log.add(new Message(mess, "Dealer"));
		//	System.out.println(mess);
			tempC = rand.nextInt(52);
			while (Cards.get(tempC).used == true) {
				tempC = rand.nextInt(52);
			}
			dCards.add(Cards.get(tempC));
			Cards.get(tempC).setUsed();
	//		System.out.println("Card " + dCards.get(dCards.size()-1).name + " of " + dCards.get(dCards.size()-1).shape + " added to the dealer's cards.");
		} else {
			int tempMax = 0;
			if (dMaxTotal <= 21) {
				tempMax = dMaxTotal;
			} else {
				tempMax = dMinTotal;
			}
			String mess = ("Dealer decided to stay! (total: " + Integer.toString(tempMax) + ")");
			Log.add(new Message(mess, "Dealer"));
			setWinner();
			dealer_turn = false;
			play_more_q = true;
		}
		dHitter = false;
	}
	
	public void refresher() {
		
		if (hit_stay_q == true) {
			bHit.setVisible(true);
			bStay.setVisible(true);
		} else {
			bHit.setVisible(false);
			bStay.setVisible(false);
		}
		
		if (dealer_turn == true) {
			if (dHitter == false)
				dealerHitStay();
		}
		
		if (play_more_q == true) {
			bYes.setVisible(true);
			bNo.setVisible(true);
		} else {
			bYes.setVisible(false);
			bNo.setVisible(false);
		}
		
		totalsChecker();
		
		if ((pMaxTotal == 21 || pMinTotal >= 21) && hit_stay_q == true) {
			int tempMax = 0;
			if (pMaxTotal <= 21) {
				tempMax = pMaxTotal;
			} else {
				tempMax = pMinTotal;
			}
			String mess = ("Auto pass! (total: " + Integer.toString(tempMax) + ")");
			Log.add(new Message(mess, "Player"));
			hit_stay_q = false;
			dealer_turn = true;
		}
		
		if ((dMaxTotal == 21 || dMinTotal >= 21) && dealer_turn == true) {
			int tempMax = 0;
			if (dMaxTotal <= 21) {
				tempMax = dMaxTotal;
			} else {
				tempMax = dMinTotal;
			}
			String mess = ("Dealer auto pass! (total: " + Integer.toString(tempMax) + ")");
			Log.add(new Message(mess, "Dealer"));
			setWinner();
			dealer_turn = false;
			play_more_q = true;
		}
		
		repaint();
	}
	
	public class Board extends JPanel {
		
		public void paintComponent(Graphics g) {
			//background
			g.setColor(colorBackground);
			g.fillRect(0, 0, aW, aH);
			
			//questions
			if (hit_stay_q == true) {
				g.setColor(Color.black);
				g.setFont(fontQuest);
				g.drawString(questHitStay, gridX+gridW+60, gridY+90);
				g.drawString("Total:", gridX+gridW+60, gridY+290);
				if (pMinTotal == pMaxTotal) {
					g.drawString(Integer.toString(pMaxTotal), gridX+gridW+60, gridY+350);
				} else if (pMaxTotal <= 21) {
					g.drawString(Integer.toString(pMinTotal) + " or " + Integer.toString(pMaxTotal), gridX+gridW+60, gridY+350);
				} else {
					g.drawString(Integer.toString(pMinTotal), gridX+gridW+60, gridY+350);
				}
			} else if (play_more_q == true) {
				g.setColor(Color.black);
				g.setFont(fontQuest);
				g.drawString(questPlayMore, gridX+gridW+70, gridY+490);
			}
		/*	
			g.setColor(Color.pink);
			g.drawRect(gridX, gridY, gridW, gridH);
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 2; j++) {
					g.drawRect(gridX+spacing+tCardW*i, gridY+spacing+tCardH*j, cardW, cardH);
				}
			}
			g.drawRect(gridX+gridW+50, gridY, 250, 400);
			g.drawRect(gridX, gridY+gridH+50, gridW, 250);
			*/
			g.setColor(Color.black);
			g.fillRect(gridX, gridY+gridH+50, gridW, 500);
			
			//Log
			g.setFont(fontLog);
			int logIndex = 0;
			for (Message L : Log) {
				if (L.getWho().equalsIgnoreCase("Dealer")) {
					g.setColor(cDealer);
				} else {
					g.setColor(cPlayer);
				}
				g.drawString(L.getMessage(), gridX+20, gridY+480+logIndex*35);
				logIndex++;
			}
			
			//score
			g.setColor(Color.BLACK);
			g.setFont(fontQuest);
			String score = ("Score: " + Integer.toString(Main.pWins) + " - " + Integer.toString(Main.dWins));
			g.drawString(score, gridX+gridW+70, gridY+gridH+300);
			
			//player cards    // pour la construction des cartes 
			int index = 0;
			for (Card c : pCards) {
				g.setColor(Color.white);
				g.fillRect(gridX+spacing+tCardW*index+rounding, gridY+spacing, cardW-rounding*2, cardH);
				g.fillRect(gridX+spacing+tCardW*index, gridY+spacing+rounding, cardW, cardH-rounding*2);
				g.fillOval(gridX+spacing+tCardW*index, gridY+spacing, rounding*2, rounding*2);
				g.fillOval(gridX+spacing+tCardW*index, gridY+spacing+cardH-rounding*2, rounding*2, rounding*2);
				g.fillOval(gridX+spacing+tCardW*index+cardW-rounding*2, gridY+spacing, rounding*2, rounding*2);
				g.fillOval(gridX+spacing+tCardW*index+cardW-rounding*2, gridY+spacing+cardH-rounding*2, rounding*2, rounding*2);
				
				g.setFont(fontCard);
				if (c.shape.equalsIgnoreCase("Hearts") || c.shape.equalsIgnoreCase("Diamonds")) {
					g.setColor(Color.red);
				} else {
					g.setColor(Color.black);
				}
				
				g.drawString(c.symbol, gridX+spacing+tCardW*index+rounding, gridY+spacing+cardH-rounding);
				
				if (c.shape.equalsIgnoreCase("Hearts")) {
					g.fillOval(gridX+tCardW*index+42, gridY+70, 35, 35);
					g.fillOval(gridX+tCardW*index+73, gridY+70, 35, 35);
					g.fillArc(gridX+tCardW*index+30, gridY+90, 90, 90, 51, 78);
				} else if (c.shape.equalsIgnoreCase("Diamonds")) {
					polyX[0] = gridX+tCardW*index+75;
					polyX[1] = gridX+tCardW*index+50;
					polyX[2] = gridX+tCardW*index+75;
					polyX[3] = gridX+tCardW*index+100;
					polyY[0] = gridY+60;
					polyY[1] = gridY+100;
					polyY[2] = gridY+140;
					polyY[3] = gridY+100;
					g.fillPolygon(polyX, polyY, 4);
				} else if (c.shape.equalsIgnoreCase("Spades")) {
					g.fillOval(gridX+tCardW*index+42, gridY+90, 35, 35);
					g.fillOval(gridX+tCardW*index+73, gridY+90, 35, 35);
					g.fillArc(gridX+tCardW*index+30, gridY+15, 90, 90, 51+180, 78);
					g.fillRect(gridX+tCardW*index+70, gridY+100, 10, 40);
				} else {
					g.fillOval(gridX+tCardW*index+40, gridY+90, 35, 35);
					g.fillOval(gridX+tCardW*index+75, gridY+90, 35, 35);
					g.fillOval(gridX+tCardW*index+58, gridY+62, 35, 35);
					g.fillRect(gridX+tCardW*index+70, gridY+75, 10, 70);
				}
				
				//-------------------------
				index++;
			}
			
			if (dealer_turn == true || play_more_q == true) {
				//dealer cards
				index = 0;
				for (Card c : dCards) {
					g.setColor(Color.white);
					g.fillRect(gridX+spacing+tCardW*index+rounding, gridY+spacing+200, cardW-rounding*2, cardH);
					g.fillRect(gridX+spacing+tCardW*index, gridY+spacing+rounding+200, cardW, cardH-rounding*2);
					g.fillOval(gridX+spacing+tCardW*index, gridY+spacing+200, rounding*2, rounding*2);
					g.fillOval(gridX+spacing+tCardW*index, gridY+spacing+cardH-rounding*2+200, rounding*2, rounding*2);
					g.fillOval(gridX+spacing+tCardW*index+cardW-rounding*2, gridY+spacing+200, rounding*2, rounding*2);
					g.fillOval(gridX+spacing+tCardW*index+cardW-rounding*2, gridY+spacing+cardH-rounding*2+200, rounding*2, rounding*2);
					
					g.setFont(fontCard);
					if (c.shape.equalsIgnoreCase("Hearts") || c.shape.equalsIgnoreCase("Diamonds")) {
						g.setColor(Color.red);
					} else {
						g.setColor(Color.black);
					}
					
					g.drawString(c.symbol, gridX+spacing+tCardW*index+rounding, gridY+spacing+cardH-rounding+200);
					
					if (c.shape.equalsIgnoreCase("Hearts")) {
						g.fillOval(gridX+tCardW*index+42, gridY+70+200, 35, 35);
						g.fillOval(gridX+tCardW*index+73, gridY+70+200, 35, 35);
						g.fillArc(gridX+tCardW*index+30, gridY+90+200, 90, 90, 51, 78);
					} else if (c.shape.equalsIgnoreCase("Diamonds")) {
						polyX[0] = gridX+tCardW*index+75;
						polyX[1] = gridX+tCardW*index+50;
						polyX[2] = gridX+tCardW*index+75;
						polyX[3] = gridX+tCardW*index+100;
						polyY[0] = gridY+60+200;
						polyY[1] = gridY+100+200;
						polyY[2] = gridY+140+200;
						polyY[3] = gridY+100+200;
						g.fillPolygon(polyX, polyY, 4);
					} else if (c.shape.equalsIgnoreCase("Spades")) {
						g.fillOval(gridX+tCardW*index+42, gridY+90+200, 35, 35);
						g.fillOval(gridX+tCardW*index+73, gridY+90+200, 35, 35);
						g.fillArc(gridX+tCardW*index+30, gridY+15+200, 90, 90, 51+180, 78);
						g.fillRect(gridX+tCardW*index+70, gridY+100+200, 10, 40);
					} else {
						g.fillOval(gridX+tCardW*index+40, gridY+90+200, 35, 35);
						g.fillOval(gridX+tCardW*index+75, gridY+90+200, 35, 35);
						g.fillOval(gridX+tCardW*index+58, gridY+62+200, 35, 35);
						g.fillRect(gridX+tCardW*index+70, gridY+75+200, 10, 70);
					}
					
					//-------------------------
					index++;
				}
				
				g.setColor(Color.black);
				g.setFont(fontQuest);
				g.drawString("Your total: ", gridX+gridW+60, gridY+40);
				if (pMaxTotal <= 21) {
					g.drawString(Integer.toString(pMaxTotal), gridX+gridW+60, gridY+120);
				} else {
					g.drawString(Integer.toString(pMinTotal), gridX+gridW+60, gridY+120);
				}
				g.drawString("Dealer's total: ", gridX+gridW+60, gridY+240);
				if (dMaxTotal <= 21) {
					g.drawString(Integer.toString(dMaxTotal), gridX+gridW+60, gridY+320);
				} else {
					g.drawString(Integer.toString(dMinTotal), gridX+gridW+60, gridY+320);
				}
			}
			
		}
		
	}
	
	public class Move implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class Click implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class ActHit implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (hit_stay_q == true) {
			//	System.out.println("You clicked 'HIT'");
				
				int tempMax = 0;
				if (pMaxTotal <= 21) {
					tempMax = pMaxTotal;
				} else {
					tempMax = pMinTotal;
				}
				String mess = ("You decided to hit! (total: " + Integer.toString(tempMax) + ")");
				Log.add(new Message(mess, "Player"));
				
				tempC = rand.nextInt(52);
				while (Cards.get(tempC).used == true) {
					tempC = rand.nextInt(52);
				}
				pCards.add(Cards.get(tempC));
				Cards.get(tempC).setUsed();
			//	System.out.println("Card " + pCards.get(pCards.size()-1).name + " of " + pCards.get(pCards.size()-1).shape + " added to the player's cards.");
			}
		}
		
	}
	
	public class ActStay implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (hit_stay_q == true) {
			//	System.out.println("You clicked 'STAY'");
				
				int tempMax = 0;
				if (pMaxTotal <= 21) {
					tempMax = pMaxTotal;
				} else {
					tempMax = pMinTotal;
				}
				String mess = ("You decided to stay! (total: " + Integer.toString(tempMax) + ")");
				Log.add(new Message(mess, "Player"));
				
				hit_stay_q = false;
				dealer_turn = true;
			}
		}
		
	}
	
	public class ActYes implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
		//	System.out.println("You clicked 'YES'");
			
			for (Card c : Cards) {
				c.setNotUsed();
			}
			
			pCards.clear();
			dCards.clear();
			Log.clear();
			
			play_more_q = false;
			hit_stay_q = true;
			
			tempC = rand.nextInt(52);
			pCards.add(Cards.get(tempC));
			Cards.get(tempC).setUsed();
	//		System.out.println("Card " + pCards.get(0).name + " of " + pCards.get(0).shape + " added to the player's cards.");
			
			tempC = rand.nextInt(52);
			while (Cards.get(tempC).used == true) {
				tempC = rand.nextInt(52);
			}
			dCards.add(Cards.get(tempC));
			Cards.get(tempC).setUsed();
	//		System.out.println("Card " + dCards.get(0).name + " of " + dCards.get(0).shape + " added to the dealer's cards.");
			
			tempC = rand.nextInt(52);
			while (Cards.get(tempC).used == true) {
				tempC = rand.nextInt(52);
			}
			pCards.add(Cards.get(tempC));
			Cards.get(tempC).setUsed();
	//		System.out.println("Card " + pCards.get(1).name + " of " + pCards.get(1).shape + " added to the player's cards.");
			
			tempC = rand.nextInt(52);
			while (Cards.get(tempC).used == true) {
				tempC = rand.nextInt(52);
			}
			dCards.add(Cards.get(tempC));
			Cards.get(tempC).setUsed();
		//	System.out.println("Card " + dCards.get(1).name + " of " + dCards.get(1).shape + " added to the dealer's cards.");
			
		}
		
	}
	
	public class ActNo implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
		//	System.out.println("You clicked 'NO'");
			Main.terminator = true;
			dispose();
		}
		
	}
	
}
