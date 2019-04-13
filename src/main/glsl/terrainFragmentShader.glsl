#version 400 core

const float SHADOW_VALUE = 0.5;

in vec2 pass_uv;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;
in vec4 shadowCoords;

out vec4 out_Color;

uniform sampler2D bgTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform vec3 lightColor[4];
uniform vec3 lightAttenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

uniform sampler2D shadowMap;

const float AMBIENT_LIGHT = 0.05;
const float TILE_FACTOR = 40.0;
const float CELL_SHADING_LEVELS = -1;// -1 to disable

void main(void) {


    //shadow
    float shadowLightFactor = 1.0;
    if (shadowCoords.x > 0 && shadowCoords.y > 0 && shadowCoords.x < 1 && shadowCoords.y < 1) {
        float nearestObjDist = texture(shadowMap, shadowCoords.xy).r;
        if (shadowCoords.z > nearestObjDist) {
            shadowLightFactor = 1.0 - SHADOW_VALUE;
            // todo ...
            if (shadowCoords.z >= 1 && nearestObjDist >= 1) { // shadowCoords.z may be > 1 but nearestObj can not
                //shadowLightFactor = 1.0;
            }
        }
    }
    // texture
    vec4 blendMapColor = texture(blendMap, pass_uv);
    float rTextureAmount = blendMapColor.r;
    float gTextureAmount = blendMapColor.g;
    float bTextureAmount = blendMapColor.b;
    float bgTextureAmount = 1 - (rTextureAmount + gTextureAmount +  bTextureAmount);
    vec2 tiledCoords = pass_uv * TILE_FACTOR;
    vec4 rTextureColor = texture(rTexture, tiledCoords) * rTextureAmount;
    vec4 gTextureColor = texture(gTexture, tiledCoords) * gTextureAmount;
    vec4 bTextureColor = texture(bTexture, tiledCoords) * bTextureAmount;
    vec4 bgTextureColor = texture(bgTexture, tiledCoords) * bgTextureAmount;
    vec4 totalColor = rTextureColor + gTextureColor + bTextureColor + bgTextureColor;
    // lights
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);
    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);
    for (int i = 0; i < 4; i++) {
        if (length(lightAttenuation[i]) == 0) break;
        float distance = length (toLightVector[i]);
        float attFactor = lightAttenuation[i].x + lightAttenuation[i].y * distance + lightAttenuation[i].z * distance * distance;
        vec3 unitLight = normalize(toLightVector[i]);
        float nDotl = dot(unitNormal, unitLight);
        float brightness = max(nDotl, 0.0);
        if (CELL_SHADING_LEVELS >= 0.0) {
            float shivtLevel = floor(brightness * CELL_SHADING_LEVELS);
            brightness = shivtLevel / CELL_SHADING_LEVELS;
        }
        vec3 lightDirection = - unitLight;
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);

        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);
        if (CELL_SHADING_LEVELS >= 0.0) {
            float shivtLevel = floor(dampedFactor * CELL_SHADING_LEVELS);
            dampedFactor = shivtLevel / CELL_SHADING_LEVELS;
        }
        totalDiffuse = totalDiffuse +  (brightness * lightColor[i])/attFactor;
        totalSpecular = totalSpecular + (dampedFactor * lightColor[i] * reflectivity)/attFactor;
    }
    totalDiffuse = max(totalDiffuse, AMBIENT_LIGHT) * shadowLightFactor;
    out_Color = vec4(totalDiffuse, 1.0) * totalColor + vec4(totalSpecular, 0.0);
    out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);// calculates the fading out to horizon
    //out_Color = vec4(shadowLightFactor);
    //out_Color = vec4(nearestObjDist);
}
