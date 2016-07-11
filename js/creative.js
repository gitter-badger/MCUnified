(function($) {
    "use strict"; // Start of use strict
    $.ajax({
      url: "https://api.github.com/repos/lizfransen/MCUnified/releases/latest",
      success: function(data) {
        console.log(data);
      },
      error: function() {
        $("#stable-release").text("Nothing Yet");
      }
    });
    $.ajax({
      url: "https://api.github.com/repos/lizfransen/MCUnified/releases",
      success: function(data) {
        var latest = $("#dev-release")
        latest.attr("href",data[0].html_url);
        latest.text(data[0].name)
      },
      error: function() {
        $("#dev-release").text("Nothing Yet");
        console.log("test");
      }
    });
    var growing = false;
    var code = $(".fa-code");
    setInterval(function(){
      growing = !growing;
      if(growing) {
        code.css("transform","scale(1.125,1.125)");
      } else {
        code.css("transform","scale(1,1)");
      }
    },400);
    // jQuery for page scrolling feature - requires jQuery Easing plugin
    $('a.page-scroll').bind('click', function(event) {
        var $anchor = $(this);
        $('html, body').stop().animate({
            scrollTop: ($($anchor.attr('href')).offset().top - 50)
        }, 1250, 'easeInOutExpo');
        event.preventDefault();
    });

    // Highlight the top nav as scrolling occurs
    $('body').scrollspy({
        target: '.navbar-fixed-top',
        offset: 51
    });

    // Closes the Responsive Menu on Menu Item Click
    $('.navbar-collapse ul li a:not(.dropdown-toggle)').click(function() {
        $('.navbar-toggle:visible').click();
    });

    // Offset for Main Navigation
    $('#mainNav').affix({
        offset: {
            top: 100
        }
    })

    // Initialize and Configure Scroll Reveal Animation
    window.sr = ScrollReveal();
    sr.reveal('.sr-icons', {
        duration: 600,
        scale: 0.3,
        distance: '0px'
    }, 200);
    sr.reveal('.sr-button', {
        duration: 1000,
        delay: 200
    });
    sr.reveal('.sr-contact', {
        duration: 600,
        scale: 0.3,
        distance: '0px'
    }, 300);

    // Initialize and Configure Magnific Popup Lightbox Plugin
    $('.popup-gallery').magnificPopup({
        delegate: 'a',
        type: 'image',
        tLoading: 'Loading image #%curr%...',
        mainClass: 'mfp-img-mobile',
        gallery: {
            enabled: true,
            navigateByImgClick: true,
            preload: [0, 1] // Will preload 0 - before current, and 1 after the current image
        },
        image: {
            tError: '<a href="%url%">The image #%curr%</a> could not be loaded.'
        }
    });

})(jQuery); // End of use strict
