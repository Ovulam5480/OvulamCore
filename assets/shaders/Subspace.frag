#define HIGHP
#define NSCALE 200.0 / 2.0
#define size 16

uniform sampler2D u_texture;
uniform sampler2D u_noise;
uniform sampler2D u_noise2;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform vec2 u_offside;
uniform float u_time;

varying vec2 v_texCoords;

void main(){
    vec2 c = v_texCoords.xy;
    vec2 coords = vec2(c.x * u_resolution.x + u_campos.x, c.y * u_resolution.y + u_campos.y);

    float btime = u_time / 5000.0;
    float noise = (texture2D(u_noise2, (coords) / NSCALE + vec2(btime) * vec2(-0.9, 0.8)).r + texture2D(u_noise, (coords) / NSCALE + vec2(btime * 1.1) * vec2(0.8, -1.0)).r) / 2.0;
    vec4 color = texture2D(u_texture, c);

    vec2 p = v_texCoords.xy - u_offside;

    float xis = mod(p.x, 3 * size);
    //if(p.x % )

    color.rgb = 0.5 + 0.5*cos(6.28318 * (noise + vec3(0.3,0.2,0.2)));

    gl_FragColor = color;
}
