import { Box, Divider, Drawer, IconButton, List, ListItem, ListItemIcon, ListItemText, Toolbar, Typography } from "@mui/material";
import { makeStyles } from "@mui/styles";
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import * as React from "react"

const useStyles = makeStyles({
    showTools: {
        left: 0,
        top: 60
    }
});

const Toolkit = () => {
    const classes = useStyles();
    const [open, setOpen] = React.useState<boolean>(localStorage.getItem('toolOpen')==="true");

    const handleDrawerOpen = () => {
        setOpen(true);
        localStorage.setItem('toolOpen', "true")  
    };

    const handleDrawerClose = () => {
        setOpen(false);
        localStorage.setItem('toolOpen', "false")  
    };

    return (
        <div style={{width: 200}}>
            <IconButton
                color="inherit"
                aria-label="open drawer"
                edge="end"
                onClick={handleDrawerOpen}
                className={classes.showTools}
                sx={{ ...(open && { display: 'none' }), position: "absolute" }}
            >
                <Typography>Show Tools</Typography><ChevronRightIcon />
            </IconButton>
            <Drawer
            anchor="left"
            open={open}
            sx={{
                width: 200,
                flexShrink: 0,
                [`& .MuiDrawer-paper`]: { width: 200, boxSizing: 'border-box' },
            }}
            variant="persistent"
            >
                <Toolbar />
                <Box sx={{ overflow: 'auto' }}>
                <IconButton onClick={handleDrawerClose}>
                    <ChevronLeftIcon />
                </IconButton>
                <List>
                    {['Show explanations', 'Use CEX', 'Change color', 'Select syntax', 'Change view'].map((text, index) => (
                    <ListItem button key={text}>
                        <ListItemText primary={text} />
                    </ListItem>
                    ))}
                </List>
                <Divider />
                <List>
                    {['Upload new', 'Merge selected', 'Clear'].map((text, index) => (
                    <ListItem button key={text}>
                        <ListItemText primary={text} />
                    </ListItem>
                    ))}
                </List>
                </Box>
            </Drawer>
        </div>
    )
}

export default Toolkit