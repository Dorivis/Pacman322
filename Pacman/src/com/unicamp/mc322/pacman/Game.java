package com.unicamp.mc322.pacman;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.HashMap;

import com.unicamp.mc322.mapa.Mapa;
import com.unicamp.mc322.pacman.funcionalities.*;
import com.unicamp.mc322.pacman.personagem.Pacman;
import com.unicamp.mc322.pacman.personagem.fantasma.*;
import com.unicamp.mc322.pacman.pontos.PontosController;
import com.unicamp.mc322.pacman.pontos.PowerUpController;
import com.unicamp.mc322.pacman.posicionamento.ParOrdenado;
import com.unicamp.mc322.pacman.posicionamento.Quadrado;
import com.unicamp.mc322.parede.ParedeController;

public class Game implements Runnable {
	private Display display;
    private boolean running;
    private Thread t;
    private int placar = 0;
    private PontosController pontosController;
    private ParedeController paredeController;
    private PowerUpController powerupController;
    private final int tamanhoTela = 512;
    private final String pathProPlanoDeFundo = "src/sprites/background/background.jpg";
    private ControleBotao controleBotao;	
    Imagem planoDeFundo;
    private Mapa mapa = new Mapa(32,32);
    private Pacman pacman;
    private FantasmaController fantasmaController;
 //   private Fantasma fantasmaAleatorio;
  //  private Fantasma fantasmaPrestigiador;
   // private FantasmaPerseguidor fantasmaPerseguidorTeste;
    //private FantasmaEvasivo fantasmaEvasivo;
    private boolean hasFinishedInit = false;
    private final int fontsize = 15;
    
    
    public synchronized void start() {
        if (running) {
        	return;
        }
        running = true;
        
        t = new Thread(this);
        t.start();
    }

    public synchronized void stop() {
        if (!running) {
            return;
        }
        
        running = false;
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
	
	@Override
    public void run() {
        init();
        while (running) {
        	if (!hasFinishedInit) continue;
        	tick();
        	display.panel.repaint();
        }
        stop();
    }
	
	public void render(Graphics g) {
		display.panel.addKeyListener(controleBotao.getKeyAdapter());  
        display.panel.setFocusable(true);
        display.panel.requestFocusInWindow();
        planoDeFundo.draw(g);
        pontosController.desenhaPontos(g);
        paredeController.desenhaParede(g);
        powerupController.desenhaPowerUp(g);
        
        //g.drawString("Vidas: ", 0, 15);
        //for (int i = 0; i<3; i++) {
        	//Imagem vidasRestantes = new Imagem("src/sprites/pacman/download.png", new ParOrdenado((3+i)*16,0), new Quadrado(new ParOrdenado((3+i)*16,0),new ParOrdenado((4+i)*16,16)));
            //vidasRestantes.draw(g);
        //}
        String stringPlacar = "Placar:";
        if (placar < 10)
        	stringPlacar += "0000" + placar;
        else if (placar < 100)
        	stringPlacar += "000" + placar;
        else if (placar < 1000)
        	stringPlacar += "00" + placar;
        else if (placar < 10000)
        	stringPlacar += "0" + placar;
        else
        	stringPlacar += placar;
        drawText(g,stringPlacar, 400, 15);
        
        powerupController.diminiuTempo();
        pacman.draw(g);
        placar += pacman.irParaProximaPosicao(paredeController, pontosController, powerupController);
        fantasmaController.move(paredeController, pacman.getTopoEsquerdo());
        fantasmaController.desenhaFantasmas(g);
        System.out.println(fantasmaController.colidiuComQuadrado(pacman.getColider()));
        if (pontosController.estaVazio() == true) {
        	planoDeFundo.draw(g);
        	drawText(g, "VOCÊ GANHOU!!!! GERANDO NOVO MAPA", 220,260);
        	this.init();
			return;
		}
        
    }
	
	//1 segundo é 1000milisegundos
	//0,05 segundos -> 50 milisegundos
	//30 segundos -> x milisegundos
	
	private void drawText(Graphics g, String string, int x, int y) {
		g.setFont(new Font("SansSerif", Font.BOLD, fontsize));
        g.setColor(Color.white);
        g.drawString(string, x, y);
	}
	
	private void tick() {
    	try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			System.err.println("Erro no sleep maroto");
			e.printStackTrace();
		}
    }
	
	private void init() {
		planoDeFundo = new Imagem(pathProPlanoDeFundo, new ParOrdenado(0,0), new Quadrado(0, 0, 512, 512));
        
        paredeController = new ParedeController();
        pontosController = new PontosController();
        powerupController = new PowerUpController();
        controleBotao = new ControleBotao();
        pacman = new Pacman(new Quadrado(16,16,2*16,2*16), "src/sprites/pacman/download.png");
        fantasmaController = new FantasmaController();
        try {
        	//mapa.getMapaAleatorio();
        	mapa.getMapaArquivo();
        	mapa.inicializaConteudoMapa(pontosController,powerupController, paredeController);
        } catch (Exception e) {
			// TODO: handle exception
		}
        hasFinishedInit = true;
        if (display == null)
        	display = new Display("PACMAN322", tamanhoTela, tamanhoTela, this);
    }
}
