package pl.dyzio.smartclockalarm.ui.elements

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pl.dyzio.smartclockalarm.SmartClockScreen

@Composable
fun SmartClockTabRow (
    allScreens : List<SmartClockScreen>,
    onTabSelected : (SmartClockScreen) -> Unit,
    currentScreen: SmartClockScreen
)
{
    Surface(
        Modifier
            .height(TabHeight + 5.dp)
            .fillMaxWidth()
    ){
        Row(Modifier.selectableGroup()) {
            allScreens.forEach { screen ->
                AlarmTab(text = stringResource(screen.title),
                    icon = screen.icon,
                    onSelected = {onTabSelected(screen)}, selected = currentScreen == screen
                )
            }
        }
    }
}

@Composable
private fun AlarmTab(
    text : String,
    icon : ImageVector,
    onSelected : () -> Unit,
    selected: Boolean
){
    val color = MaterialTheme.colors.onSurface
    val durationMillis = if (selected) TabFadeInAnimationDuration else TabFadeOutAnimationDuration
    val animSpecs = remember {
        tween<Color>(
            durationMillis = durationMillis,
            easing = LinearEasing,
            delayMillis = TabFadeInAnimationDelay
        )
    }

    val tanTintColor by animateColorAsState(
        targetValue = if (selected) color else color.copy(alpha = InactiveTabOpacity),
        animationSpec = animSpecs
    )
    Row(
        modifier = Modifier
            .padding(16.dp)
            .height(TabHeight)
            .selectable(
                selected = selected,
                onClick = onSelected,
                role = Role.Tab,
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = rememberRipple(
                    bounded = false,
                    radius = Dp.Unspecified,
                    color = Color.Unspecified
                )
            )
            .clearAndSetSemantics { contentDescription = text }
    ){
        Icon(imageVector = icon, contentDescription = text, tint = tanTintColor)
        if(selected){
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, color = tanTintColor)
        }
    }
}

private val TabHeight = 55.dp
private const val InactiveTabOpacity = 0.6f
private const val TabFadeInAnimationDuration = 150
private const val TabFadeInAnimationDelay = 100
private const val TabFadeOutAnimationDuration = 100