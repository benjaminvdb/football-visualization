package nl.liacs.sports.football.visualization;

import processing.core.PApplet;

public class Field implements Drawable {
    // Size is in cm
    float width = 105f;
    float height = 68f;

    // [m]
    float space = 0.30f;//geen idee wat dit is?
    // [m]
    float line_width = 0.10f;
    // center circle diammeter [m]
    float center_circle_diam = 9.15f * 2;
    // x penalty area size [m]
    float x_pen_area = 16.5f;
    // y penalty area size [m]
    float y_pen_area = 40.3f;
    // x penalty area size [m]
    float x_5m_area = 5.5f;
    // y penalty area size [m]
    float y_5m_area = 18.32f;
    // x goal size [m]
    float x_goal = 2.44f;
    // y goal size [m]
    float y_goal = 7.32f;
    // [m]
    float pen_dot = 11f;
    //float neutral_spot_dist = 5f; //niet nodig voor spel
    float pi = (float) Math.PI;
    // white
    int white = 255;
    // black
    int black = 0;

    int leftSideColor = 0xFF1010FF;
    int rightSideColor = 0xFFEFEF10;
    boolean invertColors = false;

    ;


    Field(float w, float h) {
        width = w;
        height = h;
    }

    public void setColorInvertion(boolean invert) {
        invertColors = invert;
    }

    public void draw(PApplet canvas, float scale) {

        canvas.background(32, 137, 4); //green

        // playing field x_size [m]
        float x_pf = width - 2 * space - line_width;
        // playing field x_size [m]
        float y_pf = height - 2 * space - line_width;

        // center circle
        canvas.stroke(white);
        canvas.noFill();
        canvas.ellipse(width * scale / 2, height * scale / 2, center_circle_diam * scale, center_circle_diam * scale);
        canvas.arc(pen_dot * scale, ((width / 2) - center_circle_diam) * scale, center_circle_diam * scale, center_circle_diam * scale, -0.55f * pi / 2, 0.55f * pi / 2);
        canvas.arc((width - pen_dot) * scale, ((width / 2) - center_circle_diam) * scale, center_circle_diam * scale, center_circle_diam * scale, 1.45f * pi / 2, 2.55f * pi / 2);

        //canvas.ellipse((width-pen_dot)*scale, height*scale/2, center_circle_diam*scale, center_circle_diam*scale);

        // left penalty area
        canvas.stroke(white);
        canvas.fill(white);
        canvas.rect((space + line_width) * scale, (height / 2 - y_pen_area / 2) * scale, x_pen_area * scale, line_width * scale);
        canvas.rect((space + line_width) * scale, (height / 2 + y_pen_area / 2) * scale, x_pen_area * scale, line_width * scale);
        canvas.rect((space + x_pen_area) * scale, (height / 2 - y_pen_area / 2) * scale, line_width * scale, y_pen_area * scale);

        // left 5 meters area
        canvas.stroke(white);
        canvas.fill(white);
        canvas.rect((space + line_width) * scale, (height / 2 - y_5m_area / 2) * scale, x_5m_area * scale, line_width * scale);
        canvas.rect((space + line_width) * scale, (height / 2 + y_5m_area / 2) * scale, x_5m_area * scale, line_width * scale);
        canvas.rect((space + x_5m_area) * scale, (height / 2 - y_5m_area / 2) * scale, line_width * scale, y_5m_area * scale);

        // right penalty area
        canvas.stroke(white);
        canvas.fill(white);
        canvas.rect((width - x_pen_area - space - line_width) * scale, (height / 2 - y_pen_area / 2) * scale, x_pen_area * scale, line_width * scale);
        canvas.rect((width - x_pen_area - space - line_width) * scale, (height / 2 + y_pen_area / 2) * scale, x_pen_area * scale, line_width * scale);
        canvas.rect((width - x_pen_area - space - line_width) * scale, (height / 2 - y_pen_area / 2) * scale, line_width * scale, y_pen_area * scale);

        // right 5 meters area
        canvas.stroke(white);
        canvas.fill(white);
        canvas.rect((width - x_5m_area - space - line_width) * scale, (height / 2 - y_5m_area / 2) * scale, x_5m_area * scale, line_width * scale);
        canvas.rect((width - x_5m_area - space - line_width) * scale, (height / 2 + y_5m_area / 2) * scale, x_5m_area * scale, line_width * scale);
        canvas.rect((width - x_5m_area - space - line_width) * scale, (height / 2 - y_5m_area / 2) * scale, line_width * scale, y_5m_area * scale);

        // field limits
        canvas.fill(white);
        canvas.stroke(white);
        canvas.rect(space * scale, space * scale, x_pf * scale, line_width * scale);  //top
        canvas.rect(space * scale, (y_pf + space) * scale, (x_pf + line_width) * scale, line_width * scale);  //bottom
        canvas.rect(space * scale, space * scale, line_width * scale, y_pf * scale);  //left
        canvas.rect((x_pf + space) * scale, space * scale, line_width * scale, y_pf * scale);  //right
        canvas.rect(((x_pf + space) / 2) * scale, space * scale, line_width * scale, y_pf * scale); //middel lijn

        // neutral spots //middenstip
        float neutral_diam = 0.5f * scale;
        canvas.stroke(white);
        canvas.fill(white);
        canvas.ellipse(width / 2 * scale, height / 2 * scale, neutral_diam, neutral_diam);  // center
        canvas.ellipse(pen_dot * scale, height / 2 * scale, neutral_diam, neutral_diam);  // penaltystip links
        canvas.ellipse((width - pen_dot) * scale, height / 2 * scale, neutral_diam, neutral_diam);  // penaltystip rechts


        // left goal
        canvas.noStroke();
        canvas.stroke((invertColors ? rightSideColor : leftSideColor)); // blue
        canvas.fill((invertColors ? rightSideColor : leftSideColor));  // blue
        canvas.rect((space - x_goal + line_width) * scale, (height / 2 - y_goal / 2) * scale, x_goal * scale, y_goal * scale);  // center

        // right goal
        canvas.stroke((invertColors ? leftSideColor : rightSideColor)); // yellow
        canvas.fill((invertColors ? leftSideColor : rightSideColor));  // yellow
        canvas.rect((width - space - line_width) * scale, (height / 2 - y_goal / 2) * scale, x_goal * scale, y_goal * scale);  // center
    }
}

