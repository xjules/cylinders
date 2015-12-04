#version 430 compatibility

float eye2db( float z ){   
    return NEARFAR_A + NEARFAR_B / z;
}

float db2eye( float z ){
    return NEARFAR_B / ( z - NEARFAR_A );
}
void main()
{
    //vec4 color = gl_Color;
    vec4 color = vec4(1.0,0.2,0.2,1.0);
    float len = length( coord );
    if (len>1.0) discard;
    //outcolor = vec4(0.7,0.7,0.7,1.0);
    //outcolor = vec4(0.2,0.2,0.2,1.0);
    
    float eye_depth = db2eye( gl_FragCoord.z );
    // Eye space offset on how much to offset z on this fragment 
    float frag_offset = frag_scale* sqrt( 1.0 - len*len );    
    // Do the subtraction in the linear eyespace, and then translate coord back to depth buffer space

    float frag_depth = eye2db(  eye_depth - frag_offset );
    
    if (frag_depth<0){
        frag_depth=0.0;
        color.rgb*=1.1;
    }
    else{
        // A VERY FAKE "lighting" model
        //float d = dot( normalize(vec3(coord,1.0)), vec3( 0.19, 0.19, 0.96225 ) );
    	float d = dot( normalize(vec3(coord,1.0)), gl_Color.xyz );
        color.rgb *= d;
        // end "lighting"
    }
    gl_FragColor = color;
    //gl_FragColor  = vec4( 0.19, 0.19, 0.96225,0.5 );
    gl_FragDepth = frag_depth;   
}
