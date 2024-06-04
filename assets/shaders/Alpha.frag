#define HIGHP

uniform sampler2D u_texture;
uniform float u_alpha;

varying vec2 v_texCoords;

void main(){
    vec4 color = texture2D(u_texture, v_texCoords);
    //用于测试
    color.r *= 0.5;
    color.a *= u_alpha;
    gl_FragColor = color;
}