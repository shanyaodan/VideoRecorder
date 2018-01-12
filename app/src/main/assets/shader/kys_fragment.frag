uniform sampler2D sTexture;
precision mediump float;
varying vec2 vTextureCoord;
void main() {
    gl_FragColor = texture2D(sTexture, vTextureCoord);
}