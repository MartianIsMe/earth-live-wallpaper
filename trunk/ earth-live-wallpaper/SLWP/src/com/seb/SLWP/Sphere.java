package com.seb.SLWP;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.util.Log;

class Sphere implements Serializable {

	private static final long serialVersionUID = -5579981347245166159L;
	private static String mTex;
	private static int intTex;
	private float moonangle = 0f;
	private int IdxCnt;
	private float langle = 0f;
	public static boolean useshading = false;
	public static float shadowcolor = 0.50f;
	private static FileTexture filetexture;
	private static FileTexture moon;
	private static FileTexture deathstar;
	// public static final Handler mHandler = new Handler();
	// Create runnable for posting
	public static final Runnable mUpdateTex = new Runnable() {
		public void run() {
			InitTex();
		}
	};

	public Sphere(Context context) {
		mContext = context;
		loadObj();
		Log.e("VboCube", "LOADED");

	}

	public static void InitTex() {
		if (gl11 == null || SLWP.Tex == 22)
			return;
		setTexture(SLWP.Tex);

		if (!mTex.equalsIgnoreCase("0")) {
			// textures.add(mTex);
			if (filetexture != null)
				filetexture.freeTex();
			filetexture = new FileTexture(gl11, mTex);
			filetexture.loadTexture();
		} else {
			if (httptexture != null)
				httptexture.freeTex();
			httptexture = new HttpTexture(gl11);
			httptexture.loadTexture();
		}
		if (intTex < 4 || intTex == 13 || intTex == 29 || intTex == 35
				|| intTex == 43 || intTex == 61) {
			if (moon != null)
				moon.freeTex();

			switch (intTex) {
			case 0:
			case 1:
			case 2:
			case 3:
				moon = new FileTexture(gl11, "moon");
				break;
			case 13:
			case 29:
				moon = new FileTexture(gl11, "deathstar");
				break;
			case 35:
				moon = new FileTexture(gl11, "yavin4");
				break;
			case 43:
				moon = new FileTexture(gl11, "moon1");
				break;
			case 61:
				moon = new FileTexture(gl11, "pandora");
				break;

			}
			moon.loadTexture();
		}
		if (textures != null)
			textures.freeTexs();
		textures = new GLTextures(gl11, mContext);
		textures.add(R.drawable.lmap);
		textures.loadTextures();
	}

	public void Init(GL10 gl) {
		if (gl == null)
			return;
		gl11 = (GL11) gl;

		InitTex();

		int[] buffer = new int[1];

		// vertex buffer.
		gl11.glGenBuffers(1, buffer, 0);
		mVertBufferIndex = buffer[0];
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertBufferIndex);
		gl11.glBufferData(GL11.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * 4,
				mVertexBuffer, GL11.GL_STATIC_DRAW);
		Log.e("VboCube", "Vx ok");

		// normal buffer.
		gl11.glGenBuffers(1, buffer, 0);
		mNormBufferIndex = buffer[0];
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mNormBufferIndex);
		gl11.glBufferData(GL11.GL_ARRAY_BUFFER, mNormBuffer.capacity() * 4,
				mNormBuffer, GL11.GL_STATIC_DRAW);
		Log.e("VboCube", "Vn ok");

		// texcoord buffer
		gl11.glGenBuffers(1, buffer, 0);
		mTexBufferIndex = buffer[0];
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mTexBufferIndex);
		gl11.glBufferData(GL11.GL_ARRAY_BUFFER, mTexBuffer.capacity() * 4,
				mTexBuffer, GL11.GL_STATIC_DRAW);
		Log.e("VboCube", "Vt ok");

		// unbind array buffer
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

		// Buffer d'indices
		gl11.glGenBuffers(1, buffer, 0);
		mIndexBufferIndex = buffer[0];
		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex);
		gl11.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER,
				mIndexBuffer.capacity() * 2, mIndexBuffer, GL11.GL_STATIC_DRAW);

		// Unbind the element array buffer.
		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		Log.e("VboCube", "Idx ok");
		IdxCnt = mIndexBuffer.capacity();
	}

	private void loadObj() {
		try {
			AssetManager am = mContext.getAssets();
			String str;
			String[] tmp;
			String[] ftmp;
			float v;
			ArrayList<Float> vlist = new ArrayList<Float>();
			ArrayList<Float> tlist = new ArrayList<Float>();
			ArrayList<Float> nlist = new ArrayList<Float>();
			ArrayList<Fp> fplist = new ArrayList<Fp>();

			BufferedReader inb = new BufferedReader(new InputStreamReader(
					am.open("earth.obj")), 1024);
			while ((str = inb.readLine()) != null) {
				tmp = str.split(" ");
				if (tmp[0].equalsIgnoreCase("v")) {

					for (int i = 1; i < 4; i++) {
						v = Float.parseFloat(tmp[i]);
						vlist.add(v);
					}

				}
				if (tmp[0].equalsIgnoreCase("vn")) {

					for (int i = 1; i < 4; i++) {
						v = Float.parseFloat(tmp[i]);
						nlist.add(v);
					}

				}
				if (tmp[0].equalsIgnoreCase("vt")) {
					for (int i = 1; i < 3; i++) {
						v = Float.parseFloat(tmp[i]);
						tlist.add(v);
					}

				}
				if (tmp[0].equalsIgnoreCase("f")) {
					for (int i = 1; i < 4; i++) {
						ftmp = tmp[i].split("/");

						long chi = Integer.parseInt(ftmp[0]) - 1;
						int cht = Integer.parseInt(ftmp[1]) - 1;
						int chn = Integer.parseInt(ftmp[2]) - 1;

						fplist.add(new Fp(chi, cht, chn));
					}
					NBFACES++;
				}
			}

			ByteBuffer vbb = ByteBuffer.allocateDirect(fplist.size() * 4 * 3);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer = vbb.asFloatBuffer();

			ByteBuffer vtbb = ByteBuffer.allocateDirect(fplist.size() * 4 * 2);
			vtbb.order(ByteOrder.nativeOrder());
			mTexBuffer = vtbb.asFloatBuffer();

			ByteBuffer nbb = ByteBuffer.allocateDirect(fplist.size() * 4 * 3);
			nbb.order(ByteOrder.nativeOrder());
			mNormBuffer = nbb.asFloatBuffer();

			for (int j = 0; j < fplist.size(); j++) {
				mVertexBuffer.put(vlist.get((int) (fplist.get(j).Vi * 3)));
				mVertexBuffer.put(vlist.get((int) (fplist.get(j).Vi * 3 + 1)));
				mVertexBuffer.put(vlist.get((int) (fplist.get(j).Vi * 3 + 2)));

				mTexBuffer.put(tlist.get(fplist.get(j).Ti * 2));
				mTexBuffer.put(tlist.get((fplist.get(j).Ti * 2) + 1));

				mNormBuffer.put(nlist.get(fplist.get(j).Ni * 3));
				mNormBuffer.put(nlist.get((fplist.get(j).Ni * 3) + 1));
				mNormBuffer.put(nlist.get((fplist.get(j).Ni * 3) + 2));
			}

			mIndexBuffer = CharBuffer.allocate(fplist.size());
			for (int j = 0; j < fplist.size(); j++) {
				mIndexBuffer.put((char) j);
			}

			mVertexBuffer.position(0);
			mTexBuffer.position(0);
			mNormBuffer.position(0);
			mIndexBuffer.position(0);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void draw(GL10 gl, boolean showmoon) {
		if (gl11 == null)
			return;
		gl.glPushMatrix();
		gl.glEnable(GL10.GL_TEXTURE_2D);

		if (!mTex.equalsIgnoreCase("0"))
			// textures.setTexture(mTex);
			filetexture.setTexture();
		else if (httptexture != null)
			httptexture.setTexture();

		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mTexBufferIndex);
		if (useshading && !mTex.equalsIgnoreCase("0")
				&& !mTex.equalsIgnoreCase("sun")) {
			// texcoord pour chaque texture (lightmap+color)
			gl11.glClientActiveTexture(GL10.GL_TEXTURE0); // lightmap
			gl11.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 0, 0);

			gl11.glClientActiveTexture(GL10.GL_TEXTURE1); // color
			gl11.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 0, 0);

			gl11.glActiveTexture(GL10.GL_TEXTURE0);
			gl11.glEnable(GL10.GL_TEXTURE_2D);
			gl11.glActiveTexture(GL10.GL_TEXTURE1);
			gl11.glEnable(GL10.GL_TEXTURE_2D);

			gl11.glColor4f(shadowcolor, shadowcolor, shadowcolor, shadowcolor);

			gl11.glActiveTexture(GL10.GL_TEXTURE0);
			textures.setTexture(R.drawable.lmap);
			gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
					GL11.GL_COMBINE);
			gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL11.GL_COMBINE_RGB,
					GL10.GL_ADD);
			gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL11.GL_SRC0_RGB,
					GL10.GL_TEXTURE);
			gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL11.GL_SRC1_RGB,
					GL11.GL_PREVIOUS);

			gl11.glActiveTexture(GL10.GL_TEXTURE1);
			// textures.setTexture(mTex);
			filetexture.setTexture();
			/* Set the texture environment mode for this texture to combine */
			gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
					GL11.GL_COMBINE);
			/* Set the method we're going to combine the two textures by. */
			gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL11.GL_COMBINE_RGB,
					GL10.GL_MODULATE);
			/* Use the previous combine texture as source 0 */
			gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL11.GL_SRC0_RGB,
					GL11.GL_PREVIOUS);
			/* Use the current texture as source 1 */
			gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL11.GL_SRC1_RGB,
					GL10.GL_TEXTURE);
			/*
			 * Set what we will operate on, in this case we are going to use
			 * just the texture colours.
			 */
			gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL11.GL_OPERAND0_RGB,
					GL10.GL_SRC_COLOR);
			gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL11.GL_OPERAND1_RGB,
					GL10.GL_SRC_COLOR);
		} else {
			gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 0, 0);
		}

		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertBufferIndex);
		gl11.glVertexPointer(3, GL10.GL_FLOAT, 0, 0);

		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mNormBufferIndex);
		gl11.glNormalPointer(GL10.GL_FLOAT, 0, 0);

		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex);
		gl11.glDrawElements(GL11.GL_TRIANGLES, IdxCnt, GL11.GL_UNSIGNED_SHORT,
				0);

		if (showmoon
				&& (intTex < 4 || intTex == 13 || intTex == 29 || intTex == 35
						|| intTex == 43 || intTex == 61)) {
			moon.setTexture();

			gl11.glRotatef(langle -= 0.2f, 0f, 0f, 1f);
			gl11.glTranslatef(3.2f, 0f, 0f);
			gl11.glScalef(0.26f, 0.26f, 0.26f);
			gl11.glRotatef(90f, 0f, 0f, 1f);
			gl11.glDrawElements(GL11.GL_TRIANGLES, IdxCnt,
					GL11.GL_UNSIGNED_SHORT, 0);
		}

		if (useshading && !mTex.equalsIgnoreCase("0")
				&& !mTex.equalsIgnoreCase("sun")) {

			gl11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			gl11.glActiveTexture(GL10.GL_TEXTURE0);
			gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
					GL10.GL_MODULATE);
			gl11.glDisable(GL10.GL_BLEND);
			gl11.glDisable(GL10.GL_TEXTURE_2D);

			gl11.glActiveTexture(GL10.GL_TEXTURE1);
			gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
					GL10.GL_MODULATE);
			gl11.glDisable(GL10.GL_BLEND);
			gl11.glDisable(GL10.GL_TEXTURE_2D);

			// gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL11.GL_OPERAND1_RGB,
			// GL10.GL_SRC_ALPHA);
		}
		gl.glPopMatrix();
		// gl11.glDisable(GL10.GL_TEXTURE_2D);
	}

	public void freeHardwareBuffers() {

		int[] buffer = new int[1];
		buffer[0] = mVertBufferIndex;
		gl11.glDeleteBuffers(1, buffer, 0);

		buffer[0] = mTexBufferIndex;
		gl11.glDeleteBuffers(1, buffer, 0);

		buffer[0] = mNormBufferIndex;
		gl11.glDeleteBuffers(1, buffer, 0);

		buffer[0] = mIndexBufferIndex;
		gl11.glDeleteBuffers(1, buffer, 0);

		mVertBufferIndex = 0;
		mIndexBufferIndex = 0;
		mTexBufferIndex = 0;
		mNormBufferIndex = 0;

		Log.d("SLWP", "sphere hardware buffer freed");

	}

	public static void setTexture(int t) {
		intTex = t;
		switch (t) {
		case 0:
			mTex = "0";
			// mTex = R.drawable.land_ocean_ice_cloud_2048;
			break;
		case 1:
			mTex = "earth_clouds";
			break;
		case 2:
			mTex = "earth_land";
			break;
		case 3:
			mTex = "earth_lights";
			break;
		case 4:
			mTex = "moon";
			break;
		case 5:
			mTex = "mars";
			break;
		case 6:
			mTex = "mercury";
			break;
		case 7:
			mTex = "venus";
			break;
		case 8:
			mTex = "jupiter";
			break;
		case 9:
			mTex = "uranus";
			break;
		case 10:
			mTex = "europa";
			break;
		case 11:
			mTex = "ganymede";
			break;
		case 12:
			mTex = "phoebe";
			break;
		case 13:
			mTex = "endor";
			break;
		case 14:
			mTex = "tatooine";
			break;
		case 15:
			mTex = "saturn";
			break;
		case 16:
			mTex = "naboo";
			break;
		case 17:
			mTex = "hoth";
			break;
		case 18:
			mTex = "geonosis";
			break;
		case 19:
			mTex = "neptune";
			break;
		case 20:
			mTex = "io";
			break;
		case 21:
			mTex = "deathstar";
			break;
		case 23:
			mTex = "mustafar";
			break;
		case 24:
			mTex = "titan";
			break;
		case 25:
			mTex = "callisto";
			break;
		case 26:
			mTex = "sun";
			break;
		case 27:
			mTex = "coruscant";
			break;
		case 28:
			mTex = "utapau";
			break;
		case 29:
			mTex = "yavin4";
			break;
		case 30:
			mTex = "kamino";
			break;
		case 31:
			mTex = "endor_clouds";
			break;
		case 32:
			mTex = "dagobah";
			break;
		case 33:
			mTex = "naboo_clouds";
			break;
		case 34:
			mTex = "bespin";
			break;
		case 35:
			mTex = "yavin";
			break;
		case 36:
			mTex = "cardassia";
			break;
		case 37:
			mTex = "ferenginar";
			break;
		case 38:
			mTex = "qonos";
			break;
		case 39:
			mTex = "romulus";
			break;
		case 40:
			mTex = "remus";
			break;
		case 41:
			mTex = "vulcan";
			break;
		case 42:
			mTex = "borg";
			break;
		case 43:
			mTex = "futuramaearth";
			break;
		case 44:
			mTex = "moon1";
			break;
		case 45:
			mTex = "mars1";
			break;
		case 46:
			mTex = "neptune1";
			break;
		case 47:
			mTex = "globetrotter";
			break;
		case 48:
			mTex = "eternium";
			break;
		case 49:
			mTex = "omicronpersei8";
			break;
		case 50:
			mTex = "trisol";
			break;
		case 51:
			mTex = "krypton";
			break;
		case 52:
			mTex = "oberon";
			break;
		case 53:
			mTex = "decapod";
			break;
		case 54:
			mTex = "cybertron";
			break;
		case 55:
			mTex = "erios";
			break;
		case 56:
			mTex = "hades";
			break;
		case 57:
			mTex = "hell";
			break;
		case 58:
			mTex = "newton";
			break;
		case 59:
			mTex = "pandora";
			break;
		case 60:
			mTex = "phele";
			break;
		case 61:
			mTex = "polyphemus";
			break;
		case 62:
			mTex = "seneka";
			break;
		case 63:
			mTex = "vergilius";
			break;
		case 64:
			mTex = "golfball";
			break;
		case 65:
			mTex = "beachball";
			break;
		case 66:
			mTex = "soccer";
			break;
		case 67:
			mTex = "oldsoccer";
			break;
		case 68:
			mTex = "softball";
			break;
		case 69:
			mTex = "arthur";
			break;
		case 70:
			mTex = "castra";
			break;
		case 71:
			mTex = "deuteros";
			break;
		case 72:
			mTex = "diyi";
			break;
		case 73:
			mTex = "drittes";
			break;
		case 74:
			
			mTex = "ektos";
			break;
		case 75:
			mTex = "erstes";
			break;
		case 76:
			mTex = "ford";
			break;
		case 77:
			mTex = "marvin";
			break;
		case 78:
			mTex = "mentos";
			break;
		case 79:
			mTex = "mervaya";
			break;
		case 80:
			mTex = "primus";
			break;
		case 81:
			mTex = "protos";
			break;
		case 82:
			mTex = "quartus";
			break;
		case 83:
			mTex = "quintus";
			break;
		case 84:
			mTex = "realisticearth";
			break;
		case 85:
			mTex = "secundus";
			break;
		case 86:
			mTex = "septimus";
			break;
		case 87:
			mTex = "sextus";
			break;
		case 88:
			mTex = "terraformedpluto";
			break;
		case 89:
			mTex = "tertius";
			break;
		case 90:
			mTex = "tetartos";
			break;
		case 91:
			mTex = "trillian";
			break;
		case 92:
			mTex = "tritos";
			break;
		case 93:
			mTex = "viertes";
			break;
		case 94:
			mTex = "vtoraya";
			break;
		case 95:
			mTex = "zaphod";
			break;
		case 96:
			mTex = "zweites";
			break;
		case 97:
			mTex = "allatu";
			break;
		case 98:
			mTex = "amoon3";
			break;
		case 99:
			mTex = "annunaki";
			break;
		case 100:
			mTex = "anu";
			break;
		case 101:
			mTex = "arazius1";
			break;
		case 102:
			mTex = "arazius2";
			break;
		case 103:
			mTex = "arazius3";
			break;
		case 104:
			mTex = "arazius4";
			break;
		case 105:
			mTex = "arazius4moon";
			break;
		case 106:
			mTex = "arazius5";
			break;
		case 107:
			mTex = "araziusasteroid";
			break;
		case 108:
			mTex = "asteroid";
			break;
		case 109:
			mTex = "batavorum";
			break;
		case 110:
			mTex = "dagan";
			break;
		case 111:
			mTex = "ellil";
			break;
		case 112:
			mTex = "elohim";
			break;
		case 113:
			mTex = "enki";
			break;
		case 114:
			mTex = "enlil";
			break;
		case 115:
			mTex = "enoch";
			break;
		case 116:
			mTex = "eridoe";
			break;
		case 117:
			mTex = "future";
			break;
		case 118:
			mTex = "hanish";
			break;
		case 119:
			mTex = "irra";
			break;
		case 120:
			mTex = "ishtar";
			break;
		case 121:
			mTex = "kishar";
			break;
		case 122:
			mTex = "kukulkan";
			break;
		case 123:
			mTex = "marduk";
			break;
		case 124:
			mTex = "moon4";
			break;
		case 125:
			mTex = "moon5";
			break;
		case 126:
			mTex = "moon8";
			break;
		case 127:
			mTex = "moon_terraformed";
			break;
		case 128:
			mTex = "nabu";
			break;
		case 129:
			mTex = "namtar";
			break;
		case 130:
			mTex = "naphidem";
			break;
		case 131:
			mTex = "nash";
			break;
		case 132:
			mTex = "nazca";
			break;
		case 133:
			mTex = "nephilim";
			break;
		case 134:
			mTex = "nergal";
			break;
		case 135:
			mTex = "ninurta";
			break;
		case 136:
			mTex = "papsukkal";
			break;
		case 137:
			mTex = "qingu";
			break;
		case 138:
			mTex = "sakkara";
			break;
		case 139:
			mTex = "sharru";
			break;
		case 140:
			mTex = "siduri";
			break;
		case 141:
			mTex = "sin";
			break;
		case 142:
			mTex = "snowballearth";
			break;
		case 143:
			mTex = "tarazed_ananda";
			break;
		case 144:
			mTex = "tarazed_aoraki";
			break;
		case 145:
			mTex = "tarazed_aphrodite";
			break;
		case 146:
			mTex = "tarazed_ashura";
			break;
		case 147:
			mTex = "tarazed_castle";
			break;
		case 148:
			mTex = "tarazed_cieloazul";
			break;
		case 149:
			mTex = "tarazed_dragonfire";
			break;
		case 150:
			mTex = "tarazed_eshq";
			break;
		case 151:
			mTex = "tarazed_esperanza";
			break;
		case 152:
			mTex = "tarazed_garden";
			break;
		case 153:
			mTex = "tarazed_honshu";
			break;
		case 154:
			mTex = "tarazed_ib";
			break;
		case 155:
			mTex = "tarazed_iib";
			break;
		case 156:
			mTex = "tarazed_iic";
			break;
		case 157:
			mTex = "tarazed_iid";
			break;
		case 158:
			mTex = "tarazed_iie";
			break;
		case 159:
			mTex = "tarazed_iowa";
			break;
		case 160:
			mTex = "tarazed_ivb";
			break;
		case 161:
			mTex = "tarazed_namaskar";
			break;
		case 162:
			mTex = "tarazed_pratyahara";
			break;
		case 163:
			mTex = "tarazed_purgatory";
			break;
		case 164:
			mTex = "tarazed_rockball";
			break;
		case 165:
			mTex = "tarazed_sys";
			break;
		case 166:
			mTex = "tarazed_vb";
			break;
		case 167:
			mTex = "tarazed_vc";
			break;
		case 168:
			mTex = "tarazed_vd";
			break;
		case 169:
			mTex = "tarazed_vib";
			break;
		case 170:
			mTex = "tarazed_vic";
			break;
		case 171:
			mTex = "tarazed_vid";
			break;
		case 172:
			mTex = "tarazed_viiib";
			break;
		case 173:
			mTex = "tarazed_viiic";
			break;
		case 174:
			mTex = "tarazed_viiid";
			break;
		case 175:
			mTex = "tarazed_viiie";
			break;
		case 176:
			mTex = "tarazed_viiif";
			break;
		case 177:
			mTex = "tarazed_wanderer";
			break;
		case 178:
			mTex = "tarazed_xib";
			break;
		case 179:
			mTex = "tarazed_xic";
			break;
		case 180:
			mTex = "tarazed_xiib";
			break;
		case 181:
			mTex = "tarazed_xiic";
			break;
		case 182:
			mTex = "tarazed_xivb";
			break;
		case 183:
			mTex = "tarazed_xivc";
			break;
		case 184:
			mTex = "tarazed_xivd";
			break;
		case 185:
			mTex = "tarazed_xixb";
			break;
		case 186:
			mTex = "tarazed_xvb";
			break;
		case 187:
			mTex = "tarazed_xvc";
			break;
		case 188:
			mTex = "tarazed_xvd";
			break;
		case 189:
			mTex = "tarazed_xve";
			break;
		case 190:
			mTex = "tarazed_xvf";
			break;
		case 191:
			mTex = "tarazed_xvg";
			break;
		case 192:
			mTex = "tarazed_xvh";
			break;
		case 193:
			mTex = "tarazed_xvib";
			break;
		case 194:
			mTex = "tarazed_xviib";
			break;
		case 195:
			mTex = "tarazed_xviic";
			break;
		case 196:
			mTex = "tarazed_xviiib";
			break;
		case 197:
			mTex = "tarazed_xviiic";
			break;
		case 198:
			mTex = "tarazed_xviiid";
			break;
		case 199:
			mTex = "tarazed_yesod";
			break;
		case 200:
			mTex = "tarazed_yetzirah";
			break;
		case 201:
			mTex = "terraformedmoon";
			break;
		case 202:
			mTex = "tiamat";
			break;
		case 203:
			mTex = "veleda";
			break;
		case 204:
			mTex = "vimana";
			break;
		case 205:
			mTex = "yukatan";
			break;
		case 206:
			mTex = "zaltu";
			break;
		}
	}

	/*
	 * public static void setTexture(int t) { intTex = t; switch (t) { case 0:
	 * mTex = 0; // mTex = R.drawable.land_ocean_ice_cloud_2048; break; case 1:
	 * mTex = R.drawable.earth_clouds; break; case 2: mTex =
	 * R.drawable.earth_land; break; case 3: mTex = R.drawable.earth_lights;
	 * break; case 4: mTex = R.drawable.moon; break; case 5: mTex =
	 * R.drawable.mars; break; case 6: mTex = R.drawable.mercury; break; case 7:
	 * mTex = R.drawable.venus; break; case 8: mTex = R.drawable.jupiter; break;
	 * case 9: mTex = R.drawable.uranus; break; case 10: mTex =
	 * R.drawable.europa; break; case 11: mTex = R.drawable.ganymede; break;
	 * case 12: mTex = R.drawable.phoebe; break; case 13: mTex =
	 * R.drawable.endor; break; case 14: mTex = R.drawable.tatooine; break; case
	 * 15: mTex = R.drawable.saturn; break; case 16: mTex = R.drawable.naboo;
	 * break; case 17: mTex = R.drawable.hoth; break; case 18: mTex =
	 * R.drawable.geonosis; break; case 19: mTex = R.drawable.neptune; break;
	 * case 20: mTex = R.drawable.io; break; case 21: mTex =
	 * R.drawable.deathstar; break; case 23: mTex = R.drawable.mustafar; break;
	 * case 24: mTex = R.drawable.titan; break; case 25: mTex =
	 * R.drawable.callisto; break; case 26: mTex = R.drawable.sun; break; case
	 * 27: mTex = R.drawable.coruscant; break; case 28: mTex =
	 * R.drawable.utapau; break; case 29: mTex = R.drawable.yavin4; break; case
	 * 30: mTex = R.drawable.kamino; break; } }
	 */

	private class Fp {
		public long Vi;
		public int Ti;
		public int Ni;

		public Fp(long chi, int ti, int ni) {
			Vi = chi;
			Ti = ti;
			Ni = ni;
			// Log.e("VboCube",Vi+"/"+Ti+"/"+Ni);
		}
	}

	public static GL11 gl11;
	private static int NBFACES = 0;
	private static GLTextures textures;
	private static HttpTexture httptexture;
	private static int mVertBufferIndex;
	private static int mNormBufferIndex;
	private static int mTexBufferIndex;
	private static int mIndexBufferIndex;
	private static FloatBuffer mVertexBuffer;
	private static FloatBuffer mTexBuffer;
	private static FloatBuffer mNormBuffer;
	private static CharBuffer mIndexBuffer;
	private static Context mContext;

}
