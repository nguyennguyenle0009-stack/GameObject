package game.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import game.keyhandler.KeyHandler;
import game.main.GamePanel;
import game.mouseclick.MouseHandler;

public class Player extends Entity {
	
	// Dùng để xử lý bàn phím và chuột
	KeyHandler keyH = new KeyHandler(gp);
	MouseHandler mouseH = new MouseHandler(gp);
	
	// Vị trí nhân vật trên màn hình (luôn ở giữa)
    private final int screenX;
    private final int screenY;

	public Player(GamePanel gp, KeyHandler keyH, MouseHandler mounseH) {
		super(gp);
		
		this.keyH = keyH;
		this.mouseH = mounseH;
		
        this.screenX = gp.getScreenWidth() / 2 - (gp.getTileSize() / 2);//360
        this.screenY = gp.getScreenHeight() / 2 - (gp.getTileSize() / 2);//264
        
        setCollisionArea(new Rectangle( 8, 16, 32, 32));
		
		setDefaultValue();
		getImagePlayer();
	}
	
	public void getImagePlayer() {
		try {
			setUp1(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_up_1.png")));
			setUp2(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_up_2.png"))); 
			setDown1(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_down_1.png"))); 
			setDown2(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_down_2.png"))); 
			setLeft1(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_left_1.png"))); 
			setLeft2(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_left_2.png"))); 
			setRight1(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_right_1.png"))); 
			setRight2(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_right_2.png"))); 
		}catch (Exception e) {
			e.getStackTrace();
		}
	}
	
	public void setDefaultValue() {
		setWorldX(23 * gp.getTileSize()); 
		setWorldY(21 * gp.getTileSize());
		setSpeed(4);
		setDirection("down");
		setSpriteCouter(0);
		setSpriteNum(1);
	}
	
	@Override
	public void update() {
	    if(mouseH.moving == true) {
	    	updateClickMove();
	    }
	    else {
	    	updateKeyboard();
	    }
	}
	
	private void updateKeyboard() {
		if(keyH.isUpPressed() == true || keyH.isDownPressed() == true 
				|| keyH.isLeftPressed() == true || keyH.isRightPressed() == true) {
			if(keyH.isUpPressed() == true) { 
				setDirection("up");
			}
			if(keyH.isDownPressed() == true) { 
				setDirection("down");
			}
			if(keyH.isLeftPressed() == true) { 
				setDirection("left");
			}
			if(keyH.isRightPressed() == true) { 
				setDirection("right");
			}
			checkCollision();
			moveIfCollisionNotDetected();
			checkAndChangeSpriteAnimation();
		}
	}
	
	private void updateClickMove() {
	    float dx = mouseH.targetX - getWorldX();
	    float dy = mouseH.targetY - getWorldY();
	    float dist = (float) Math.sqrt(dx * dx + dy * dy);

	    // Chọn hướng mặt
	    if (Math.abs(dx) > Math.abs(dy)) {
	        setDirection(dx < 0 ? "left" : "right");
	    } else {
	        setDirection(dy < 0 ? "up" : "down");
	    }

	    if (dist > getSpeed()) {
	        // Vị trí giả lập sau khi bước
	        int nextWorldX = (int) (getWorldX() + (dx / dist * getSpeed()));
	        int nextWorldY = (int) (getWorldY() + (dy / dist * getSpeed()));

	        // Lấy collision area gốc
	        Rectangle area = getCollisionArea();

	        // Tính cạnh của vùng va chạm tại vị trí mới
	        int leftWorldX = nextWorldX + area.x;
	        int rightWorldX = nextWorldX + area.x + area.width;
	        int topWorldY = nextWorldY + area.y;
	        int bottomWorldY = nextWorldY + area.y + area.height;

	        int tileSize = gp.getTileSize();

	        int leftCol = leftWorldX / tileSize;
	        int rightCol = rightWorldX / tileSize;
	        int topRow = topWorldY / tileSize;
	        int bottomRow = bottomWorldY / tileSize;
	        // Giả sử bạn có mảng tileMngr.mapTileNum[row][col] và tileMngr.tile[].collision
	        boolean canMoveX = true;
	        boolean canMoveY = true;
	        // Kiểm tra va chạm trục X
	        if (dx < 0) { // sang trái
	            if (gp.getTileManager().getTile()[ gp.getTileManager().getMapTileNumber()[topRow][leftCol] ].isCollision() ||
	                gp.getTileManager().getTile()[ gp.getTileManager().getMapTileNumber()[bottomRow][leftCol] ].isCollision()) {
	                canMoveX = false;
	            }
	        } else if (dx > 0) { // sang phải
	            if (gp.getTileManager().getTile()[ gp.getTileManager().getMapTileNumber()[topRow][rightCol] ].isCollision() ||
	                gp.getTileManager().getTile()[ gp.getTileManager().getMapTileNumber()[bottomRow][rightCol] ].isCollision()) {
	                canMoveX = false;
	            }
	        }
	        // Kiểm tra va chạm trục Y
	        if (dy < 0) { // lên
	            if (gp.getTileManager().getTile()[ gp.getTileManager().getMapTileNumber()[topRow][leftCol]].isCollision() ||
	                gp.getTileManager().getTile()[ gp.getTileManager().getMapTileNumber()[topRow][rightCol] ].isCollision()) {
	                canMoveY = false;
	            }
	        } else if (dy > 0) { // xuống
	            if (gp.getTileManager().getTile()[ gp.getTileManager().getMapTileNumber()[bottomRow][leftCol] ].isCollision() ||
	                gp.getTileManager().getTile()[ gp.getTileManager().getMapTileNumber()[bottomRow][rightCol] ].isCollision()) {
	                canMoveY = false;
	            }
	        }
	        // Cập nhật vị trí chỉ khi không va chạm
	        if (canMoveX) setWorldX(nextWorldX);
	        if (canMoveY) setWorldY(nextWorldY);
	    } else {
	        // Nếu quá gần đích thì ghim luôn
	        setWorldX(mouseH.targetX);
	        setWorldY(mouseH.targetY);
	        mouseH.moving = false;
	    }

	    checkAndChangeSpriteAnimation();

	    // Nếu người chơi bấm phím, hủy auto-move
	    if (keyH.isUpPressed() || keyH.isDownPressed() || keyH.isLeftPressed() || keyH.isRightPressed()) {
	        mouseH.moving = false;
	    }
	}

	
	private void checkCollision() {
		setCollisionOn(false);
		gp.getCheckCollision().checkTile(this);
	}
	
	private void moveIfCollisionNotDetected() {
//		int rightOffset = gp.getScreenWidth() - screenX;
//		int x = checkCharacterPositionAtXAxis(rightOffset);
//		int botOffSet = gp.getScreenHeight() - screenY;
//		int y = checkCharacterPositionAtYAxis(botOffSet);
		if(isCollisionOn() == false) {
			switch(getDirection()) {
			case "up":
				setWorldY(getWorldY() - getSpeed());
//				System.out.println("getWorldY:" + getWorldY());
//				System.out.println("getWorldX:" + getWorldX());
//				System.out.println("X:" + x);
//				System.out.println("Y:" + y);
				break;
			case "down":
				setWorldY(getWorldY() + getSpeed());
				break;
			case "left":
				setWorldX(getWorldX() - getSpeed());
				break;
			case "right":
				setWorldX(getWorldX() + getSpeed());
				break;
			}
		}
	}
	
	public void checkAndChangeSpriteAnimation() {
		setSpriteCouter(getSpriteCouter() + 1);
		if(getSpriteCouter() > 10) {
			if(getSpriteNum() == 1) {
				setSpriteNum(2);
			}
			else if(getSpriteNum() == 2) {
				setSpriteNum(1);
			}
			setSpriteCouter(0);
		}
	}
	
	@Override
	public void draw(Graphics2D g2) {
		int rightOffset = gp.getScreenWidth() - screenX;
		int x = checkCharacterPositionAtXAxis(rightOffset);
		int botOffSet = gp.getScreenHeight() - screenY;
		int y = checkCharacterPositionAtYAxis(botOffSet);
		g2.drawImage(getDirectionImage(), x, y, gp.getTileSize(), gp.getTileSize(), null);

	}
	
	//Kiểm tra màn hình có bị tràn ra khỏi bản đồ theo trục ngang không
	private int checkCharacterPositionAtXAxis(int rightOffset) {
		// Giới hạn khi nhân vật ở quá sát rìa trái bản đồ (camera không thể dịch sang trái hơn)
		if(screenX > getWorldX()) {
			// giới hạn tọa độ bên phải của nhân vật/camera trong bản đồ
			return getWorldX();
		}
		// rightOffset = khoảng cách từ nhân vật đến mép phải màn hình
		// Giới hạn khi camera ở quá sát rìa phải bản đồ (màn hình tràn ra ngoài)
		if(rightOffset > gp.getWorldWidth() - getWorldX()) {
			// vị trí nhân vật trên màn hình khi camera bị ghim ở mép phải bản đồ (bù trừ phần màn hình bị tràn).
			return gp.getScreenWidth() - (gp.getWorldWidth() - getWorldX());
		}
		// Nếu màn hình không bị tràn ra khỏi bản đồ trả về bình thường
		return screenX;
	}
	
	private int checkCharacterPositionAtYAxis(int botOffset) {
		if(screenY > getWorldY()) {
			return getWorldY();
		}
		if(botOffset > gp.getWorldHeight() - getWorldY()) {
			return gp.getScreenHeight()	- (gp.getWorldHeight() - getWorldY());
		}
		return screenY;
	}

	private BufferedImage getDirectionImage() {
		BufferedImage image = null;
		switch(getDirection()) {
		case "up":
			if(getSpriteNum() == 1) { image = getUp1(); }
			if(getSpriteNum() == 2) { image = getUp2(); }
			break;
		case "down":
			if(getSpriteNum() == 1) { image = getDown1(); }
			if(getSpriteNum() == 2) { image = getDown2(); }
			break;
		case "left":
			if(getSpriteNum() == 1) { image = getLeft1(); }
			if(getSpriteNum() == 2) { image = getLeft2(); }
			break;
		case "right":
			if(getSpriteNum() == 1) { image = getRight1(); }
			if(getSpriteNum() == 2) { image = getRight2(); }
			break;
		}
		return image;
	}

	public int getScreenX() {
		return screenX;
	}

	public int getScreenY() {
		return screenY;
	}
	
	
}















