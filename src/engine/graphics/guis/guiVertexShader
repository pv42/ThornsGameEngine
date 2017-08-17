# version 140

in vec2 position;

out vec2 uv;

uniform mat4 localTransformationMatrix;

void main(void) {
    gl_Position = localTransformationMatrix * vec4(position, 0.0, 1.0);
    uv = vec2((position.x+1.0)/2.0, 1 - (position.y+1.0)/2.0);
}