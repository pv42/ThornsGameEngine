#version 400 core

in vec2 pass_uv;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D diffTexture;
uniform sampler2D specularMap;
uniform float usesSpecularMap;
uniform vec3 lightColor[4];
uniform vec3 lightAttenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

uniform float ambient_light;

const float CELL_SHADING_LEVELS = -1; // -1 to disable

void main(void) {
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);
    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);
    for (int i = 0; i < 4; i++) {
        if(length(lightAttenuation[i]) == 0) break; //not a light
        float distance = length (toLightVector[i]);
        float attFactor = lightAttenuation[i].x + lightAttenuation[i].y * distance + lightAttenuation[i].z * distance * distance;
        vec3 unitLight = normalize(toLightVector[i]);
        float nDotl = dot(unitNormal, unitLight);
        float brightness = max(nDotl,0.0 );
        if(CELL_SHADING_LEVELS >= 0.0) {
            float shivtLevel = floor(brightness * CELL_SHADING_LEVELS);
            brightness = shivtLevel / CELL_SHADING_LEVELS;
        }

        vec3 lightDirection = - unitLight;
        vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);

        float specularFactor = dot(reflectedLightDirection,unitVectorToCamera);
        specularFactor = max(specularFactor,0.0);
        float dampedFactor = pow(specularFactor, shineDamper);
        if(CELL_SHADING_LEVELS >= 0.0) {
                    float shivtLevel = floor(dampedFactor * CELL_SHADING_LEVELS);
                    dampedFactor = shivtLevel / CELL_SHADING_LEVELS;
        }
        totalDiffuse  = totalDiffuse + (brightness * lightColor[i]) / attFactor;
        totalSpecular = totalSpecular + (dampedFactor * lightColor[i] * reflectivity )/ attFactor;
    }
    totalDiffuse = max(totalDiffuse , ambient_light);
    //totalDiffuse = vec3(0.5);

    vec4 textureColor = texture(diffTexture,pass_uv);
    if(textureColor.a < 0.5) {
        discard;
    }

    if(usesSpecularMap > 0.5) {
        vec4 mapInfo = texture(specularMap, pass_uv);
        totalSpecular *= mapInfo.r;
    }
    out_Color = vec4(totalDiffuse,1.0) * textureColor + vec4( totalSpecular, 0.0);
    out_Color = mix(vec4(skyColor,1.0), out_Color, visibility);
    //out_Color = vec4(totalDiffuse, 1.0);
}
