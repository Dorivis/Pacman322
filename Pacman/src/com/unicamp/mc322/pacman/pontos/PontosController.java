package com.unicamp.mc322.pacman.pontos;
import java.awt.Graphics;
import java.util.ArrayList;

import com.unicamp.mc322.pacman.posicionamento.ParOrdenado;
import com.unicamp.mc322.pacman.posicionamento.Quadrado;

public class PontosController {
	private ArrayList<Pontos> pontosNoMapa;
	//private ArrayList<Pontos> pontosColetados;
	
	public PontosController() {
		pontosNoMapa = new ArrayList();
	}
	
	public void adicionarPonto(Pontos p) {
		this.pontosNoMapa.add(p);
	}
	
	public int getPosPonto(ParOrdenado coord) {
		int pos = 0;
		for (Pontos p: pontosNoMapa) {
			if (p.getPosicao().equals(coord))
				break;
			pos++;
		}
		return pos;
	}
	
	//Mudar com relação ao colider??? Falar com o bird
	public int getPontos(ParOrdenado coord) {
		//Comeu o ponto 'x'
		int ponto = 0;
		int pos = getPosPonto(coord);
		try {
			ponto = pontosNoMapa.get(pos).getPonto();
			pontosNoMapa.remove(pos);
		}
		catch (IndexOutOfBoundsException e) {
			System.out.println("Posição inválida");
		}
		return ponto;
	}
	
	public void desenhaPontos(Graphics g) {
		for (Pontos p: pontosNoMapa) {
			p.desenhaPonto(g);
		}
	}
}
