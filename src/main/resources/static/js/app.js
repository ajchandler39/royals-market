// Attach the CSRF token to all HTMX requests so POSTs pass Spring Security.
document.addEventListener('htmx:configRequest', function (evt) {
    var token = document.querySelector('meta[name="_csrf"]');
    var header = document.querySelector('meta[name="_csrf_header"]');
    if (token && header && header.content) {
        evt.detail.headers[header.content] = token.content;
    }
});

// Register the auction countdown as an Alpine component so x-data="countdown('<iso>')" works.
document.addEventListener('alpine:init', function () {
    window.Alpine.data('countdown', function (endIso) {
        return {
            label: '',
            ended: false,
            tick: function () {
                var diff = new Date(endIso).getTime() - Date.now();
                if (diff <= 0) {
                    this.label = 'Auction ended';
                    this.ended = true;
                    return;
                }
                var s = Math.floor(diff / 1000);
                var d = Math.floor(s / 86400); s -= d * 86400;
                var h = Math.floor(s / 3600); s -= h * 3600;
                var m = Math.floor(s / 60); s -= m * 60;
                var parts = [];
                if (d > 0) parts.push(d + 'd');
                parts.push(h + 'h', m + 'm', s + 's');
                this.label = parts.join(' ');
            },
            init: function () {
                this.tick();
                var self = this;
                setInterval(function () { self.tick(); }, 1000);
            }
        };
    });

    // Listing form: toggles SALE/AUCTION fields and shows only the stat fields that apply
    // to the currently selected catalog item (read from the <option>'s data attributes).
    window.Alpine.data('listingForm', function (initialType) {
        return {
            type: initialType || 'SALE',
            stats: [],   // applicable stat names for the selected item
            gear: false,
            syncItem: function (select) {
                var opt = select && select.selectedOptions ? select.selectedOptions[0] : null;
                if (!opt || !opt.value) { this.stats = []; this.gear = false; return; }
                var s = opt.getAttribute('data-stats') || '';
                this.stats = s ? s.split(',') : [];
                this.gear = opt.getAttribute('data-gear') === 'true';
            }
        };
    });
});
