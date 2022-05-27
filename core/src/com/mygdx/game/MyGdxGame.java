package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.w3c.dom.Text;

import java.util.Random;
import java.util.SortedMap;

public class MyGdxGame extends ApplicationAdapter {
	Texture img;
	//Variáveis das imagens do jogo
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Texture telaInicial;
	private Texture goldCoin;
	private Texture silverCoin;

	//Colisores dos objetos
	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Rectangle retanguloCanoCima;
	private Rectangle retanguloCanoBaixo;
	private Circle circuloGoldCoin;
	private Circle circuloSilverCoin;

	//Variáveis que estão definindo as funções do jogo
	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variacao = 0;
	private float gravidade = 2;
	private float posicaoInicialVerticalPassaro = 0;
	private float posicaoCanoHorizontal;
	private float posicaoCanoVertical;
	private float espacoEntreCanos;
	private Random random;
	private int pontos = 0;
	private int pontuacaoMaxima = 0;
	private boolean passouCano = false;
	private int estadoJogo = 0;
	private float posicaoHorizontalPassaro = 0;
	private float posicaoHorizontalGoldCoin = 0;
	private float posicaoVerticalGoldCoin = 0;
	private float posicaoHorizontalSilverCoin = 0;
	private float posicaoVerticalSilverCoin = 0;

	//Fonte de texto
	BitmapFont textoPontuacao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuacao;

	//Sons do jogo
	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;

	//Guardando as preferências
	Preferences preferences;

	//Variáveis da camera e tamanho da tela
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 720;
	private final float VIRTUAL_HEIGHT = 1280;

	//Criando as texturas e objetos do jogo
	@Override
	public void create () {
		inicializarTexturas();
		inicializarObjetos();
	}

	//Renderizando os métodos e funções dentro dele
	@Override
	public void render () {
		//Limpando o buffet
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		//Métodos utilizados
		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();
	}

	//Método está chamando todas as texturas
	private void inicializarTexturas(){
		passaros = new Texture[3];
		passaros[0] = new Texture("angrybird_1.png");
		passaros[1] = new Texture("angrybird_2.png");
		passaros[2] = new Texture("angrybird_1.png");

		//Imagens que aparecem no cenário, e obstáculos e moedas
		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");
		telaInicial = new Texture("Inicial_angry_bird.png");
		goldCoin = new Texture("goldcoin.png");
		silverCoin = new Texture("silvercoin.png");

	}

	//Método está inicializando todos os objetos que pertecem o jogo
	private void inicializarObjetos(){
		//
		batch = new SpriteBatch();
		random = new Random();

		//Definição do tamanho da tela e espaçamento entre os canos
		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVerticalPassaro = alturaDispositivo / 2;
		posicaoCanoHorizontal = larguraDispositivo;
		espacoEntreCanos = 350;

		posicaoHorizontalGoldCoin = larguraDispositivo;
		posicaoVerticalGoldCoin = random.nextInt((int) alturaDispositivo);
		posicaoHorizontalSilverCoin = larguraDispositivo;
		posicaoVerticalSilverCoin = random.nextInt((int) alturaDispositivo);

		//Definindo a fonte da pontuação, cor e tamanho
		textoPontuacao = new BitmapFont();
		textoPontuacao.setColor(Color.WHITE);
		textoPontuacao.getData().setScale(10);

		//Definindo a fonte da reiniciar, cor e tamanho
		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(Color.GREEN);
		textoReiniciar.getData().setScale(2);

		//Definindo a fonte da melhor pontuação, cor e tamanho
		textoMelhorPontuacao = new BitmapFont();
		textoMelhorPontuacao.setColor(Color.RED);
		textoMelhorPontuacao.getData().setScale(2);

		//Colisões dos objetos
		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoBaixo = new Rectangle();
		retanguloCanoCima = new Rectangle();
		circuloGoldCoin = new Circle();
		circuloSilverCoin = new Circle();

		//Pegando os sons do arquivo
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

		//Pegando as preferências
		preferences = Gdx.app.getPreferences("flappyBird");
		pontuacaoMaxima = preferences.getInteger("pontuacaoMaxima", 0);

		//Ajustando a camêra
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2,0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
	}

	//Verificando em que estado o jogador se encontra no jogo
	private void verificarEstadoJogo(){

		//Pegando quando a tela é tocada
		boolean toqueTela = Gdx.input.justTouched();

		//Estado inicial do jogo, quando abre ele e não tem nada acontecendo
		if (estadoJogo == 0) {
			if (toqueTela) {
				gravidade = -15;
				estadoJogo = 1;
				somVoando.play();
			}

		//Estado de jogo, onde o jogador está mexendo
		}else if(estadoJogo == 1) {
			if (toqueTela) {
				gravidade = -15;
				somVoando.play();
			}

			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
			posicaoHorizontalGoldCoin -= Gdx.graphics.getDeltaTime() * 200;
			posicaoHorizontalSilverCoin -= Gdx.graphics.getDeltaTime() * 200;

			if(posicaoHorizontalGoldCoin <= -goldCoin.getWidth()){
				posicaoHorizontalGoldCoin = larguraDispositivo;
				posicaoVerticalGoldCoin = random.nextInt((int) alturaDispositivo);
			}

			if(posicaoHorizontalSilverCoin <= -silverCoin.getWidth()){
				posicaoHorizontalSilverCoin = larguraDispositivo;
				posicaoVerticalSilverCoin = random.nextInt((int) alturaDispositivo);
			}

			//Os canos aparecem depois de passarem pela tela
			if (posicaoCanoHorizontal < -canoTopo.getWidth()) {
				posicaoCanoHorizontal = larguraDispositivo;
				posicaoCanoVertical = random.nextInt(400) - 200;
				passouCano = false;
			}

			//Pássaro se movimenta para frente e com o toque do jogador e cai com a gravidade
			if (posicaoInicialVerticalPassaro > 0 || toqueTela) {
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
				gravidade++;
			}

		//Último estado quando o jogador perde e mostra a pontuação dele e a melhor pontuação do jogo
		}else if (estadoJogo == 2){
			if (pontos > pontuacaoMaxima){
				pontuacaoMaxima = pontos;
				preferences.putInteger("pontuacaoMaxima", pontuacaoMaxima);
				preferences.flush();
			}
			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime()*500;

			//Resetando jogo
			if (toqueTela){
				estadoJogo = 0;
				pontos = 0;
				gravidade = 0;
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturaDispositivo / 2;
				posicaoCanoHorizontal = larguraDispositivo;
			}
		}
	}

	//Método onde detecta as colisões e passa para o estado "2"
	private void detectarColisoes(){
		//Detecta a colisão do pássaro na frente dele
		circuloPassaro.set(
				50 + posicaoHorizontalPassaro + passaros[0].getWidth() / 2,
				posicaoInicialVerticalPassaro + passaros[0].getHeight() / 2,
				passaros[0].getWidth() / 2
		);

		circuloGoldCoin.set(
				posicaoHorizontalGoldCoin + goldCoin.getWidth() / 2,
				posicaoVerticalGoldCoin + goldCoin.getHeight() / 2,
				goldCoin.getWidth() / 2
		);
		circuloSilverCoin.set(
				posicaoHorizontalSilverCoin + silverCoin.getWidth() / 2,
				posicaoVerticalSilverCoin + silverCoin.getHeight() / 2,
				silverCoin.getWidth() / 2
		);

		//Colisão do cano de baixo
		retanguloCanoBaixo.set(
				posicaoCanoHorizontal,
				alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);

		//Colisão do cano de cima
		retanguloCanoCima.set(
				posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical,
				canoTopo.getWidth(), canoTopo.getHeight()
		);

		//Verificando as colisões
		boolean colidiuCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
		boolean colidiuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);
		boolean colidiuMoedaOuro = Intersector.overlaps(circuloPassaro, circuloGoldCoin);
		boolean colidiuMoedaPrata = Intersector.overlaps(circuloPassaro, circuloSilverCoin);

		//Se colidiu vai para o último estado
		if (colidiuCanoCima || colidiuCanoBaixo){
			if (estadoJogo == 1){
				somColisao.play();
				estadoJogo = 2;
			}
		}

		if(colidiuMoedaOuro){
			pontos += 10;
			somPontuacao.play();

			posicaoHorizontalGoldCoin = larguraDispositivo;
			posicaoVerticalGoldCoin = random.nextInt((int) alturaDispositivo);
		}

		if(colidiuMoedaPrata){
			pontos += 5;
			somPontuacao.play();

			posicaoHorizontalSilverCoin = larguraDispositivo;
			posicaoVerticalSilverCoin = random.nextInt((int) alturaDispositivo);
		}
	}

	//Método faz aparecer os assets do jogo na tela do jogador
	private void desenharTexturas(){
		//Projetando a câmera
		batch.setProjectionMatrix(camera.combined);
		//Início do jogo
		batch.begin();
		//Desenhando as texturas que foram definidas anteriormente
		batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
		batch.draw(silverCoin, posicaoHorizontalSilverCoin, posicaoVerticalSilverCoin);
		batch.draw(goldCoin, posicaoHorizontalGoldCoin, posicaoVerticalGoldCoin);
		batch.draw(passaros[(int) variacao], 50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro);
		batch.draw(canoBaixo,posicaoCanoHorizontal,alturaDispositivo/2-canoBaixo.getHeight()-espacoEntreCanos/2+posicaoCanoVertical);
		batch.draw(canoTopo,posicaoCanoHorizontal,alturaDispositivo/2+espacoEntreCanos/2+posicaoCanoVertical);
		textoPontuacao.draw(batch,String.valueOf(pontos),larguraDispositivo/2,alturaDispositivo -110);

		if(estadoJogo == 0){
			batch.draw(telaInicial, 0, 0, larguraDispositivo, alturaDispositivo);
		}

		//Desenha os textos de pontuação e informações
		if(estadoJogo == 2){
			batch.draw(gameOver,larguraDispositivo/2 - gameOver.getWidth()/2,alturaDispositivo/2);
			textoReiniciar.draw(batch,"Toque para reiniciar!",larguraDispositivo/2 - 140,alturaDispositivo/2 - gameOver.getHeight()/2);
			textoMelhorPontuacao.draw(batch,"Seu record é "+pontuacaoMaxima+" pontos",larguraDispositivo/2 - 140,alturaDispositivo/2-gameOver.getHeight());
		}
		//Finaliza os desenhos do jogo
		batch.end();
	}

	//Método está validando os pontos do jogador toda vez que ele passa pelos canos
	public void validarPontos(){
		//Se o pássaro passou pelo cano, ele ganha pontos
		if(posicaoCanoHorizontal < 50-passaros[0].getWidth()){
			if(!passouCano){
				pontos++;
				passouCano = true;
				somPontuacao.play();
			}
		}

		variacao += Gdx.graphics.getDeltaTime() * 10;

		//Verifica o cache do jogo e se estiver muito alto, ele zera
		if(variacao > 3){
			variacao = 0;
		}
	}

	//Ajusta o tamanho
	@Override
	public void resize(int width,int height){
		viewport.update(width, height);
	}

	//Limpa quando fecha
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
