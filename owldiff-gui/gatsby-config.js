module.exports = {
  siteMetadata: {
    siteUrl: "https://www.yourdomain.tld",
    title: "OWLDiff-GUI",
  },
  plugins: [
    {
      resolve: `gatsby-plugin-manifest`,
      options: {
        name: "OWLDiff web app",
        short_name: "OWLDiff",
        start_url: "/",
        background_color: "#379fbf",
        theme_color: "#379fbf",
        display: "standalone",
        icon: "src/images/favicon.gif",
        crossOrigin: `use-credentials`,
      }
    }
  ],
};
