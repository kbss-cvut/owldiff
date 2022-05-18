import * as React from "react"
import Layout from "../components/Layout"
import {Typography} from "@mui/material";

const AboutPage = () => {

    return (
        <Layout pageTitle="About OWLDiff">
            <Typography variant={"h4"}>About OWLDiff</Typography>
            <Typography variant={"body1"}>OWLDiff is a project aiming at providing diff/merge functionality for OWL
                ontologies.</Typography>
            <br/>
            <Typography variant={"h4"}>About this module</Typography>
            <Typography variant={"body1"}>This extension to OWLDiff was developed as bachelor work. Main goal was to
                turn OWLDiff into fully functional website with available API.</Typography>
        </Layout>
    )
}

export default AboutPage