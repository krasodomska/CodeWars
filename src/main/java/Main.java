import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static List<List<String>> splitString;
    public static HashMap<Integer, List<Place>> map = new HashMap<>();

    public static void main(String[] args) {
        String a = String.join("\n", new String[]
                {
                        "+---+------------+---+",
                        "|                    |",
                        "+---+------------+---+",
                        "|   |            |   |",
                        "|   |            |   |",
                        "|   |            |   |",
                        "|   |            |   |",
                        "+   +------------+   +",
                        "|                    |",
                        "+---+------------+---+"
                });
        String b = String.join("\n", new String[]
                {
"+-----------------+",
"|                 |",
"|   +-------------+",
"|   |              ",
"|   |              ",
"|   |              ",
"|   +-------------+",
"|                 |",
"|                 |",
"+-----------------+"
                });
        System.out.println("frames");
        for(String frame : process(a)){
            System.out.println(frame);
        }
        System.out.println("table");
        splitString.forEach(x -> {
            x.forEach(System.out::print);
            System.out.println();
        });
    }

    public static String[] process(String shape) {
        splitString(shape);
        colorAllFreePlace();
        deleteBackground();
        List<String> finalList = new ArrayList<>();
        map.keySet().forEach(color -> {
            String frame = createBox(color);
            finalList.add(frame);
        });
        return finalList.toArray(new String[finalList.size()]);
    }

    static String createBox(Integer color) {
        String[][] boxPlane = new String[splitString.size()][splitString.get(0).size()];
        for (String[] line : boxPlane) {
            Arrays.fill(line, " ");
        }
        for (Place place : map.get(color)) {
            boxPlane[place.y][place.x] = "" + color;
        }
        List<List<String>> box = Arrays.stream(boxPlane).map(line -> Arrays.stream(line).collect(Collectors.toList())).collect(Collectors.toList());
        createFrame(box);
        boxToLeft(box, false);
        box = trimRight(box);
        return box.stream().map(line -> line.stream().map(letter -> {
                    if (letter.matches("\\d*")) {
                        return " ";
                    } else return letter;
                }
        ).collect(Collectors.joining())).collect(Collectors.joining("\n"));
    }

    static List<List<String>> boxToLeft(List<List<String>> box, boolean leftAlign) {
        for (List<String> line : box) {
            if (!line.get(0).equals(" ")) {
                leftAlign = true;
                break;
            }
        }
        if (leftAlign) return box;
        box.forEach(line -> line.remove(0));
        return boxToLeft(box, leftAlign);
        //return  boxToLeft(box, leftAlign);

    }

    static List<List<String>> trimRight(List<List<String>> box) {
        for (List line : box) {
            {
                for (int i = line.size() - 1; i >= 0; i--) {
                    if (line.get(i).equals(" ")) line.remove(i);
                    else break;
                }
            }
        }
        return box.stream().filter(line -> line.size() > 0).collect(Collectors.toList());
    }

    static void createFrame(List<List<String>> box) {
        for (int y = 0; y < box.size() - 1; y++) {
            for (int x = 0; x < box.get(y).size() - 1; x++) {
                if (box.get(y).get(x).matches("\\d*")) {
                    if (box.get(y + 1).get(x).equals(" ")){
                        if(box.get(y + 1).get(x - 1).matches("\\d") || box.get(y + 1).get(x + 1).matches("\\d") )
                            box.get(y + 1).set(x, "+");
                        else box.get(y + 1).set(x, "-");
                    }
                    if (box.get(y - 1).get(x).equals(" ")) box.get(y - 1).set(x, "-");
                    if (box.get(y).get(x + 1).equals(" ")){
                        if(box.get(y + 1).get(x + 1).matches("\\d")) box.get(y).set(x + 1, "+");
                        else box.get(y).set(x + 1, "|");
                    }
                    if (box.get(y).get(x - 1).equals(" ")){
                        if(box.get(y + 1).get(x - 1).matches("\\d")) box.get(y).set(x - 1, "+");
                        else  box.get(y).set(x - 1, "|");
                    }
                }
            }
        }
        for (int y = 0; y < box.size() - 1; y++) {
            for (int x = 0; x < box.get(y).size() - 1; x++) {
                if (box.get(y).get(x).matches("\\d*")) {
                    if (box.get(y + 1).get(x + 1).equals(" ")) box.get(y + 1).set(x + 1, "+");
                    if (box.get(y - 1).get(x - 1).equals(" ")) box.get(y - 1).set(x - 1, "+");
                    if (box.get(y + 1).get(x - 1).equals(" ")) box.get(y + 1).set(x - 1, "+");
                    if (box.get(y - 1).get(x + 1).equals(" ")) box.get(y - 1).set(x + 1, "+");
                }
            }
        }
    }

    static void splitString(String shape) {
        String[] firstStepSplit = shape.split("\n");
        splitString = Arrays.stream(firstStepSplit).map(line -> Arrays.asList(line.split(""))).collect(Collectors.toList());
    }

    static void deleteBackground() {
        HashSet<Integer> colorToDelete = new HashSet<>();
        for (Integer color : map.keySet()) {
            boolean amIBackground = false;
            for (Place place : map.get(color))
                if (place.x == 0 || place.y == 0
                        || place.x == splitString.get(place.y).size() - 1
                        || place.y == splitString.size() - 1) {
                    amIBackground = true;
                    break;
                }
            if (amIBackground) colorToDelete.add(color);
        }
        for (Integer color : colorToDelete) {
            map.remove(color);
        }

    }

    static void colorAllFreePlace() {
        Place spacePlace = findSpace();
        Integer color = 1;
        map.clear();
        while (spacePlace != null) {
            paintTheWorld(spacePlace, color);
            color++;
            spacePlace = findSpace();
        }

    }

    static void paintTheWorld(Place place, Integer color) {
        splitString.get(place.y).set(place.x, color.toString());
//         add to map

        if (map.containsKey(color)) {
            map.get(color).add(place);
        } else {
            List<Place> list = new LinkedList();
            list.add(place);
            map.put(color, list);
        }
        //up
        if (place.y > 0) {
            if (splitString.get(place.y - 1).get(place.x).equals(" "))
                paintTheWorld(new Place(place.x, place.y - 1), color);
        }
        //down
        if (place.y < splitString.size() - 1) {
            if (splitString.get(place.y + 1).get(place.x).equals(" "))
                paintTheWorld(new Place(place.x, place.y + 1), color);
        }
        //left
        if (place.x > 0) {
            if (splitString.get(place.y).get(place.x - 1).equals(" "))
                paintTheWorld(new Place(place.x - 1, place.y), color);
        }
        //right
        if (place.x < splitString.get(place.y).size() - 1) {
            if (splitString.get(place.y).get(place.x + 1).equals(" "))
                paintTheWorld(new Place(place.x + 1, place.y), color);
        }

    }

    static Place findSpace() {
        Place place = new Place();
        splitString.forEach(line -> line.forEach(x -> {
            if (x.equals(" ")) {
                place.setX(line.indexOf(x));
                place.setY(splitString.indexOf(line));
                place.setHaveCoordinate(true);
                return;
            }
        }));

        return (place.haveCoordinate) ? place : null;
    }

    static class Place {
        int x;
        int y;
        boolean haveCoordinate;

        public Place() {

        }

        public Place(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public void setHaveCoordinate(boolean flag) {
            haveCoordinate = flag;
        }
    }

}