#ifdef GL_ES
    precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
    vec2 pixelSize = vec2(1.0 / textureSize(u_texture, 0));
    vec2 uv = v_texCoords - mod(v_texCoords, pixelSize) + 0.5 * pixelSize;

    gl_FragColor = texture2D(u_texture, uv);
}


