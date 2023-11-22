package kek.team.kokline.users

import csstype.Position
import csstype.px
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML


val app = FC<Props> {
    ReactHTML.h1 {
        +"Hello, React+Kotlin/JS! App module"
    }
    ReactHTML.div {
        css {
            position = Position.absolute
            top = 10.px
            right = 10.px
        }
        ReactHTML.h3 {
            +"John Doe: Building and breaking things"
        }
        ReactHTML.img {
            src = "https://via.placeholder.com/640x360.png?text=Video+Player+Placeholder"
        }
    }
}
