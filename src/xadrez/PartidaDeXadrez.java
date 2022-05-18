package xadrez;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tabuleirojogo.Peca;
import tabuleirojogo.Posicao;
import tabuleirojogo.Tabuleiro;
import xadrez.pecas.Bispo;
import xadrez.pecas.Cavalo;
import xadrez.pecas.Peao;
import xadrez.pecas.Rainha;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaDeXadrez {

	private int turno;
	private Cor jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean xeque;
	private boolean xequeMate;
	private PecaDeXadrez enPassantVuneravel;
	private PecaDeXadrez promovido;
	
	private List<Peca> pecasNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapturadas = new ArrayList<>();
	
	public PartidaDeXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cor.BRANCO;
		ConfigInicial();
	}
	
	public int getTurno() {
		return turno;
	}
	
	public Cor getJogadorAtual() {
		return jogadorAtual;
	}
	
	public boolean getXeque() {
		return xeque;
	}
	
	public boolean getXequeMate(){
		return xequeMate;
	}
	
	public PecaDeXadrez getEnPassantVuneravel() {
		return enPassantVuneravel;
	}

	public PecaDeXadrez getPromovido() {
		return promovido;
	}
	
	public PecaDeXadrez[][] getPecas(){
		PecaDeXadrez[][] mat = new PecaDeXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for(int i=0; i<tabuleiro.getLinhas(); i++) {
			for(int j=0; j<tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaDeXadrez) tabuleiro.peca(i,j);
			}
		}
		return mat;
	}
	
	public boolean[][] possiveisMovimentos(PosicaoDoXadrez posicaoOrigem){
		Posicao posicao = posicaoOrigem.posicionar();
		validarPosicaoDeOrigem(posicao);
		return tabuleiro.peca(posicao).possiveisMovimentos();
	}
	
	public PecaDeXadrez executarMovimento(PosicaoDoXadrez posicaoDeOrigem, PosicaoDoXadrez posicaoDeDestino) {
		Posicao origem = posicaoDeOrigem.posicionar();
		Posicao destino = posicaoDeDestino.posicionar();
		validarPosicaoDeOrigem(origem);
		validarPosicaoDeDestino(origem, destino);
		Peca pecaCapturada = mover(origem, destino);
		
		if(testarXeque(jogadorAtual)) {
			desfazerMovimento(origem, destino, pecaCapturada);
			throw new XadrezException("Você não pode se colocar em xeque");
		}
		
		PecaDeXadrez pecaQueMoveu = (PecaDeXadrez)tabuleiro.peca(destino);
		
		//#movimento especial promoção
		promovido = null;
		if(pecaQueMoveu instanceof Peao) {
			if(pecaQueMoveu.getCor() == Cor.BRANCO && destino.getLinha() == 0 || pecaQueMoveu.getCor() == Cor.PRETO && destino.getLinha() == 7) {
				promovido = (PecaDeXadrez)tabuleiro.peca(destino);
				promovido = substituirPecaPromovida("Q");
			}
		}
		
		xeque = (testarXeque(inimigo(jogadorAtual))) ? true : false;
		
		if(testarXequeMate(inimigo(jogadorAtual))) {
			xequeMate = true;
		}
		else {
			proximoTurno();
		}
		
		//#movimento especial en passant
		if(pecaQueMoveu instanceof Peao && (destino.getLinha() == origem.getLinha() - 2 || destino.getLinha() == origem.getLinha() +2)) {
			enPassantVuneravel = pecaQueMoveu;
		}
		else {
			enPassantVuneravel = null;
		}
		
		return (PecaDeXadrez) pecaCapturada;
	}
	
	public PecaDeXadrez substituirPecaPromovida(String tipo) {
		if(promovido == null) {
			throw new IllegalStateException("Não há peça para ser promovida");
		}
		if(!tipo.equals("B") && !tipo.equals("C") && !tipo.equals("T") && !tipo.equals("Q")) {
			throw new InvalidParameterException("Tipo inválido para a promoção");
		}
		
		Posicao pos = promovido.getPosicaoDoXadrez().posicionar();
		Peca p = tabuleiro.removerPeca(pos);
		pecasNoTabuleiro.remove(p);
		
		PecaDeXadrez novaPeca = novaPeca(tipo, promovido.getCor());
		tabuleiro.colocarPeca(novaPeca, pos);
		pecasNoTabuleiro.add(novaPeca);
		
		return novaPeca;
	}
	
	private PecaDeXadrez novaPeca(String tipo, Cor cor) {
		if(tipo.endsWith("B")) return new Bispo(tabuleiro, cor);
		if(tipo.endsWith("C")) return new Cavalo(tabuleiro, cor);
		if(tipo.endsWith("Q")) return new Rainha(tabuleiro, cor);
		return new Torre(tabuleiro, cor);
	}
	
	private Peca mover(Posicao origem, Posicao destino) {
		PecaDeXadrez p = (PecaDeXadrez)tabuleiro.removerPeca(origem);
		p.aumentarContagemMovimentos();
		Peca pecaCapturada = tabuleiro.removerPeca(destino);
		tabuleiro.colocarPeca(p, destino);
		
		if(pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}
		
		//#movimento especial roque lado do rei (roque pequeno)
		if(p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removerPeca(origemT);
			tabuleiro.colocarPeca(torre, destinoT);
			torre.aumentarContagemMovimentos();
		}
		
		//#movimento especial roque lado da rainha (roque grande)
		if(p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removerPeca(origemT);
			tabuleiro.colocarPeca(torre, destinoT);
			torre.aumentarContagemMovimentos();
		}
		
		//#movimento especial en passant
		if(p instanceof Peao) {
			if(origem.getColuna() != destino.getColuna() && pecaCapturada == null) {
				Posicao posicaoPeao;
				if(p.getCor() == Cor.BRANCO) {
					posicaoPeao = new Posicao(destino.getLinha() + 1, destino.getColuna());
				}
				else {
					posicaoPeao = new Posicao(destino.getLinha() - 1, destino.getColuna());
				}
				pecaCapturada = tabuleiro.removerPeca(posicaoPeao);
				pecasCapturadas.add(pecaCapturada);
				pecasNoTabuleiro.remove(pecaCapturada);
			}
		}
		
		return pecaCapturada;
	}
	
	private void desfazerMovimento(Posicao origem, Posicao destino, Peca pecaCapturada) {
		PecaDeXadrez p = (PecaDeXadrez)tabuleiro.removerPeca(destino);
		p.diminuirContagemMovimentos();
		tabuleiro.colocarPeca(p, origem);
		
		if(pecaCapturada != null) {
			tabuleiro.colocarPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}
		
		//#movimento especial roque lado do rei (roque pequeno)
		if(p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removerPeca(destinoT);
			tabuleiro.colocarPeca(torre, origemT);
			torre.diminuirContagemMovimentos();
		}
		
		//#movimento especial roque lado da rainha (roque grande)
		if(p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removerPeca(destinoT);
			tabuleiro.colocarPeca(torre, origemT);
			torre.diminuirContagemMovimentos();
		}
		
		//movimento especial en passant
		if(p instanceof Peao) {
			if(origem.getColuna() != destino.getColuna() && pecaCapturada == enPassantVuneravel) {
				PecaDeXadrez peao = (PecaDeXadrez)tabuleiro.removerPeca(destino);
				Posicao posicaoPeao;
				if(p.getCor() == Cor.BRANCO) {
					posicaoPeao = new Posicao(3, destino.getColuna());
				}
				else {
					posicaoPeao = new Posicao(4, destino.getColuna());
				}
				tabuleiro.colocarPeca(peao, posicaoPeao);
			}
		}
		
	}
	
	private void validarPosicaoDeOrigem(Posicao posicao) {
		if(!tabuleiro.haUmaPeca(posicao)) {
			throw new XadrezException("Não existe uma peça na posição de origem");
		}
		if(jogadorAtual != ((PecaDeXadrez)tabuleiro.peca(posicao)).getCor()) {
			throw new XadrezException("A peça escolhida não é sua");
		}
		if(!tabuleiro.peca(posicao).existeMovimentoPossivel()) {
			throw new XadrezException("Não existe movimentos possíveis para a peça escolhida");
		}
	}
	
	private void validarPosicaoDeDestino(Posicao origem, Posicao destino) {
		if(!tabuleiro.peca(origem).podeMover(destino)) {
			throw new XadrezException("A peça escolhida não pode se mover para a posição do destino");
		}
	}
	
	private void proximoTurno() {
		turno++;
		jogadorAtual = (jogadorAtual == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}
	
	private Cor inimigo(Cor cor) {
		return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}
	
	private PecaDeXadrez rei(Cor cor) {
		List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == cor).collect(Collectors.toList());
		for(Peca p : lista) {
			if(p instanceof Rei) {
				return (PecaDeXadrez)p;
			}
		}
		throw new IllegalStateException("Não existe o rei " + cor + " no tabuleiro");
	}
	
	private boolean testarXeque(Cor cor) {
		Posicao posicaoRei = rei(cor).getPosicaoDoXadrez().posicionar();
		List<Peca> pecasInimigas = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == inimigo(cor)).collect(Collectors.toList());
		for(Peca p : pecasInimigas) {
			boolean[][] mat = p.possiveisMovimentos();
			if(mat[posicaoRei.getLinha()][posicaoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testarXequeMate(Cor cor){
		if(!testarXeque(cor)) {
			return false;
		}
		List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == cor).collect(Collectors.toList());
		for(Peca p : lista) {
			boolean[][] mat = p.possiveisMovimentos();
			for(int i=0; i<tabuleiro.getLinhas(); i++) {
				for(int j=0; j<tabuleiro.getColunas(); j++) {
					if(mat[i][j]) {
						Posicao origem = ((PecaDeXadrez)p).getPosicaoDoXadrez().posicionar();
						Posicao destino = new Posicao(i, j);
						Peca pecaCapturada = mover(origem, destino);
						boolean testarXeque = testarXeque(cor);
						desfazerMovimento(origem, destino, pecaCapturada);
						if(!testarXeque) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private void colocarNovaPeca(char coluna, int linha, PecaDeXadrez peca) {
		tabuleiro.colocarPeca(peca, new PosicaoDoXadrez(coluna, linha).posicionar());
		pecasNoTabuleiro.add(peca);
	}
	
	private void ConfigInicial() {
		colocarNovaPeca('a', 1, new Torre(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('b', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('c', 1, new Bispo(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('d', 1, new Rainha(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('f', 1, new Bispo(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('h', 1, new Torre(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('a', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('b', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('c', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('d', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('e', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('f', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('g', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('h', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		
		colocarNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
		colocarNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));
		colocarNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
		colocarNovaPeca('d', 8, new Rainha(tabuleiro, Cor.PRETO));
		colocarNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
		colocarNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));
		colocarNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
		colocarNovaPeca('a', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('b', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('c', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('d', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('e', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('f', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('g', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('h', 7, new Peao(tabuleiro, Cor.PRETO, this));
	}
	
}
