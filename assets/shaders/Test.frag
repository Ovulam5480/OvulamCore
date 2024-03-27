uniform sampler2D u_texture;

varying vec2 v_texCoords;
void main(){
    vec2 t = v_texCoords.xy;
    vec4 color = texture2D(u_texture, t);
    color.rgb = vec3(1,1,0);
    gl_FragColor = color;
}