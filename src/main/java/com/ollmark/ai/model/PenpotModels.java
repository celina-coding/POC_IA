package com.ollmark.ai.models;

import java.util.List;

class PenpotPage {
    private String type = "add-page";
    private String id;
    private String name;
    
    public String getType() { return type; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

class PenpotRectangle {
    private String type = "add-obj";
    private String id;
    private String pageId;
    private RectangleObject obj;
    
    public String getType() { return type; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }
    public RectangleObject getObj() { return obj; }
    public void setObj(RectangleObject obj) { this.obj = obj; }
}

class RectangleObject {
    private String type = "rect";
    private String id;
    private String name;
    private double x;
    private double y;
    private double width;
    private double height;
    private SelectionRect selrect;
    private List<Point> points;
    private Transform transform;
    private List<Fill> fills;
    private String parentId = "00000000-0000-0000-0000-000000000000";
    private String frameId = "00000000-0000-0000-0000-000000000000";
    
    public String getType() { return type; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    public SelectionRect getSelrect() { return selrect; }
    public void setSelrect(SelectionRect selrect) { this.selrect = selrect; }
    public List<Point> getPoints() { return points; }
    public void setPoints(List<Point> points) { this.points = points; }
    public Transform getTransform() { return transform; }
    public void setTransform(Transform transform) { this.transform = transform; }
    public List<Fill> getFills() { return fills; }
    public void setFills(List<Fill> fills) { this.fills = fills; }
    public String getParentId() { return parentId; }
    public String getFrameId() { return frameId; }
}

class PenpotText {
    private String type = "add-obj";
    private String id;
    private String pageId;
    private TextObject obj;
    
    public String getType() { return type; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }
    public TextObject getObj() { return obj; }
    public void setObj(TextObject obj) { this.obj = obj; }
}

class TextObject {
    private String type = "text";
    private String id;
    private String name;
    private double x;
    private double y;
    private double width;
    private double height;
    private TextContent content;
    private String growType = "fixed";
    private String fontFamily = "Inter";
    private String fontSize = "24";
    private String fontWeight = "700";
    private String fontStyle = "normal";
    private String lineHeight = "1.2";
    private String letterSpacing = "0";
    private String textAlign = "center";
    private SelectionRect selrect;
    private List<Point> points;
    private Transform transform;
    private List<Fill> fills;
    private String parentId = "00000000-0000-0000-0000-000000000000";
    private String frameId = "00000000-0000-0000-0000-000000000000";
    
    public String getType() { return type; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    public TextContent getContent() { return content; }
    public void setContent(TextContent content) { this.content = content; }
    public String getGrowType() { return growType; }
    public String getFontFamily() { return fontFamily; }
    public String getFontSize() { return fontSize; }
    public String getFontWeight() { return fontWeight; }
    public String getFontStyle() { return fontStyle; }
    public String getLineHeight() { return lineHeight; }
    public String getLetterSpacing() { return letterSpacing; }
    public String getTextAlign() { return textAlign; }
    public SelectionRect getSelrect() { return selrect; }
    public void setSelrect(SelectionRect selrect) { this.selrect = selrect; }
    public List<Point> getPoints() { return points; }
    public void setPoints(List<Point> points) { this.points = points; }
    public Transform getTransform() { return transform; }
    public void setTransform(Transform transform) { this.transform = transform; }
    public List<Fill> getFills() { return fills; }
    public void setFills(List<Fill> fills) { this.fills = fills; }
    public String getParentId() { return parentId; }
    public String getFrameId() { return frameId; }
}

class TextContent {
    private String type = "root";
    private List<ContentChild> children;
    
    public String getType() { return type; }
    public List<ContentChild> getChildren() { return children; }
    public void setChildren(List<ContentChild> children) { this.children = children; }
}

class ContentChild {
    private String type = "paragraph-set";
    private List<Paragraph> children;
    
    public String getType() { return type; }
    public List<Paragraph> getChildren() { return children; }
    public void setChildren(List<Paragraph> children) { this.children = children; }
}

class Paragraph {
    private String type = "paragraph";
    private List<TextSpan> children;
    
    public String getType() { return type; }
    public List<TextSpan> getChildren() { return children; }
    public void setChildren(List<TextSpan> children) { this.children = children; }
}

class TextSpan {
    private String text;
    private String fillColor = "#000000";
    private String fontSize = "24";
    private String fontWeight = "700";
    private String fontFamily = "Inter";
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getFillColor() { return fillColor; }
    public void setFillColor(String fillColor) { this.fillColor = fillColor; }
    public String getFontSize() { return fontSize; }
    public void setFontSize(String fontSize) { this.fontSize = fontSize; }
    public String getFontWeight() { return fontWeight; }
    public void setFontWeight(String fontWeight) { this.fontWeight = fontWeight; }
    public String getFontFamily() { return fontFamily; }
    public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }
}

class SelectionRect {
    private double x;
    private double y;
    private double width;
    private double height;
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    public double getX1() { return x1; }
    public void setX1(double x1) { this.x1 = x1; }
    public double getY1() { return y1; }
    public void setY1(double y1) { this.y1 = y1; }
    public double getX2() { return x2; }
    public void setX2(double x2) { this.x2 = x2; }
}

class Point {
    private double x;
    private double y;
    
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
}

class Transform {
    private double a = 1;
    private double b = 0;
    private double c = 0;
    private double d = 1;
    private double e = 0;
    private double f = 0;
    
    public double getA() { return a; }
    public double getB() { return b; }
    public double getC() { return c; }
    public double getD() { return d; }
    public double getE() { return e; }
    public double getF() { return f; }
}

class Fill {
    private String fillColor;
    private double fillOpacity = 1.0;
    
    public String getFillColor() { return fillColor; }
    public void setFillColor(String fillColor) { this.fillColor = fillColor; }
    public double getFillOpacity() { return fillOpacity; }
    public void setFillOpacity(double fillOpacity) { this.fillOpacity = fillOpacity; }
}