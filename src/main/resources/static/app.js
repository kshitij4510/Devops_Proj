// AgriRent Frontend JavaScript Application
let currentRole = 'FARMER';
let currentTab = 'equipment';
let equipmentData = [];
let rentalData = [];
let selectedEquipmentForBooking = null;

// Initialize on DOM load
document.addEventListener('DOMContentLoaded', () => {
    fetchEquipment();
    fetchRentals();
    fetchDashboardStats();

    // Polling for live dashboard refresh every 10 seconds
    setInterval(fetchDashboardStats, 10000);
});

// Role Switcher
function changeRole() {
    currentRole = document.getElementById('role-select').value;
    showToast(`Switched active role to: ${currentRole}`);
    renderEquipment();
    renderRentals();
}

// Tab Switching
function switchTab(tab) {
    currentTab = tab;
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-pane').forEach(pane => pane.classList.remove('active'));

    document.getElementById(`tab-${tab}`).classList.add('active');
    document.getElementById(`pane-${tab}`).classList.add('active');

    if (tab === 'rentals') {
        fetchRentals();
    } else {
        fetchEquipment();
    }
}

// Fetch Equipment Catalog
async function fetchEquipment() {
    try {
        const res = await fetch('/api/equipment');
        if (res.ok) {
            equipmentData = await res.json();
            renderEquipment();
        }
    } catch (e) {
        console.error('Error fetching equipment:', e);
    }
}

// Render Equipment Catalog
function renderEquipment(listToRender = null) {
    const container = document.getElementById('equipment-container');
    const items = listToRender || equipmentData;
    container.innerHTML = '';

    if (items.length === 0) {
        container.innerHTML = `<div style="grid-column: 1/-1; text-align: center; padding: 3rem; color: var(--text-muted);">
            <h3>No agricultural machinery found matching your criteria.</h3>
            <p>Try resetting filters or registering new equipment.</p>
        </div>`;
        return;
    }

    items.forEach(eq => {
        const card = document.createElement('div');
        card.className = 'equipment-card';
        card.id = `eq-card-${eq.id}`;

        const isAvailable = eq.status === 'AVAILABLE';
        const canBook = (currentRole === 'FARMER' || currentRole === 'ADMIN') && isAvailable;

        card.innerHTML = `
            <div class="card-top">
                <span class="eq-category-badge">${eq.category}</span>
                <span class="status-pill status-${eq.status}">${eq.status}</span>
            </div>
            <h3 class="card-title">${escapeHtml(eq.name)}</h3>
            <p class="card-desc">${escapeHtml(eq.description || 'Modern agricultural machinery.')}</p>
            <div class="card-meta">
                <span>📍 Location: <strong>${escapeHtml(eq.location)}</strong></span>
                <span>👤 Owner: <strong>${escapeHtml(eq.ownerName)}</strong> (${escapeHtml(eq.ownerContact || 'N/A')})</span>
            </div>
            <div class="card-rates-bar">
                <div class="rate-item">
                    <span class="rate-label">Daily Rental</span>
                    <span class="rate-amt">₹${eq.dailyRate}</span>
                </div>
                <div class="rate-item" style="text-align: right;">
                    <span class="rate-label">Hourly Rental</span>
                    <span class="rate-amt">₹${eq.hourlyRate}</span>
                </div>
            </div>
            <div class="card-footer">
                <span style="font-size: 0.8rem; color: var(--text-muted);">ID: #${eq.id}</span>
                ${canBook ? `
                    <button class="btn btn-primary btn-sm btn-book" id="btn-book-${eq.id}" onclick="openBookModal(${eq.id})">
                        Book Rental
                    </button>
                ` : `
                    <button class="btn btn-secondary btn-sm" disabled style="opacity: 0.6;">
                        ${isAvailable ? 'View Only' : eq.status}
                    </button>
                `}
            </div>
        `;
        container.appendChild(card);
    });
}

// Fetch Rentals
async function fetchRentals() {
    try {
        const res = await fetch('/api/rentals');
        if (res.ok) {
            rentalData = await res.json();
            renderRentals();
            updatePendingBadge();
        }
    } catch (e) {
        console.error('Error fetching rentals:', e);
    }
}

// Render Rentals Table with Role-based Actions
function renderRentals() {
    const tbody = document.getElementById('rentals-tbody');
    tbody.innerHTML = '';

    if (rentalData.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9" style="text-align: center; color: var(--text-muted); padding: 2rem;">No rental records present.</td></tr>`;
        return;
    }

    rentalData.forEach(r => {
        const tr = document.createElement('tr');
        tr.id = `rental-row-${r.id}`;

        let actionButtons = '';
        if (currentRole === 'OWNER' || currentRole === 'ADMIN') {
            if (r.rentalStatus === 'PENDING') {
                actionButtons = `
                    <button class="btn btn-success btn-sm" id="btn-approve-${r.id}" onclick="updateRentalStatus(${r.id}, 'approve')">Approve</button>
                    <button class="btn btn-danger btn-sm" id="btn-reject-${r.id}" onclick="updateRentalStatus(${r.id}, 'reject')">Reject</button>
                `;
            } else if (r.rentalStatus === 'APPROVED') {
                actionButtons = `
                    <button class="btn btn-primary btn-sm" id="btn-dispatch-${r.id}" onclick="updateRentalStatus(${r.id}, 'dispatch')">Dispatch (In-Use)</button>
                `;
            } else if (r.rentalStatus === 'IN_USE') {
                actionButtons = `
                    <button class="btn btn-secondary btn-sm" id="btn-return-${r.id}" onclick="updateRentalStatus(${r.id}, 'return')">Confirm Return</button>
                `;
            } else {
                actionButtons = `<span style="color: var(--text-muted); font-size: 0.85rem;">Completed</span>`;
            }
        } else {
            actionButtons = `<span style="color: var(--text-muted); font-size: 0.85rem;">Farmer View Only</span>`;
        }

        tr.innerHTML = `
            <td><strong>#${r.id}</strong></td>
            <td>${escapeHtml(r.equipmentName || 'Equipment #' + r.equipmentId)}</td>
            <td>${escapeHtml(r.renterName)}</td>
            <td>${escapeHtml(r.renterContact)}</td>
            <td>${escapeHtml(r.farmLocation)}</td>
            <td>${r.durationDays} Days</td>
            <td><strong style="color: var(--accent-emerald);">₹${r.totalCost || 0}</strong></td>
            <td><span class="status-pill status-${r.rentalStatus}">${r.rentalStatus}</span></td>
            <td>${actionButtons}</td>
        `;
        tbody.appendChild(tr);
    });
}

// Update Pending Badge in tab
function updatePendingBadge() {
    const pendingCount = rentalData.filter(r => r.rentalStatus === 'PENDING').length;
    const badge = document.getElementById('pending-count');
    badge.innerText = pendingCount;
    badge.style.display = pendingCount > 0 ? 'inline-block' : 'none';
}

// Fetch Dashboard Stats
async function fetchDashboardStats() {
    try {
        const res = await fetch('/api/dashboard/stats');
        if (res.ok) {
            const data = await res.json();
            document.getElementById('stat-total-equipment').innerText = data.totalEquipment || 0;
            document.getElementById('stat-available-equipment').innerText = data.availableEquipment || 0;
            document.getElementById('stat-active-rentals').innerText = data.activeRentals || 0;
            document.getElementById('stat-total-revenue').innerText = '₹' + Number(data.totalRevenue || 0).toLocaleString();
            document.getElementById('stat-utilization-rate').innerText = (data.utilizationRate || 0) + '%';
        }
    } catch (e) {
        console.error('Error fetching dashboard stats:', e);
    }
}

// Search and Filter Handling
function handleSearch() {
    const query = document.getElementById('search-input').value.trim().toLowerCase();
    const category = document.getElementById('category-filter').value;
    const status = document.getElementById('status-filter').value;

    const filtered = equipmentData.filter(eq => {
        const matchesQuery = !query || 
            (eq.name && eq.name.toLowerCase().includes(query)) ||
            (eq.location && eq.location.toLowerCase().includes(query)) ||
            (eq.description && eq.description.toLowerCase().includes(query));
        const matchesCat = !category || eq.category === category;
        const matchesStatus = !status || eq.status === status;
        return matchesQuery && matchesCat && matchesStatus;
    });

    renderEquipment(filtered);
}

function handleFilter() {
    handleSearch();
}

function resetFilters() {
    document.getElementById('search-input').value = '';
    document.getElementById('category-filter').value = '';
    document.getElementById('status-filter').value = '';
    renderEquipment();
}

// Modals
function openAddEquipmentModal() {
    document.getElementById('modal-add-equipment').classList.add('active');
}

function openBookModal(equipmentId) {
    const eq = equipmentData.find(e => e.id === equipmentId);
    if (!eq) return;

    selectedEquipmentForBooking = eq;
    document.getElementById('book-eq-id').value = eq.id;
    document.getElementById('book-eq-name').innerText = eq.name;
    document.getElementById('book-eq-rate').innerText = `Rate: ₹${eq.dailyRate} / day`;

    const today = new Date().toISOString().split('T')[0];
    document.getElementById('input-rental-start').value = today;
    document.getElementById('input-rental-start').min = today;
    document.getElementById('input-rental-days').value = 2;

    calculateRentalTotal();
    document.getElementById('modal-book-rental').classList.add('active');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}

function calculateRentalTotal() {
    if (!selectedEquipmentForBooking) return;
    const days = parseInt(document.getElementById('input-rental-days').value) || 1;
    const total = days * selectedEquipmentForBooking.dailyRate;
    document.getElementById('book-estimated-cost').innerText = '₹' + total.toLocaleString();
}

// Submit Add Equipment
async function submitAddEquipment(e) {
    e.preventDefault();
    const payload = {
        name: document.getElementById('input-eq-name').value.trim(),
        category: document.getElementById('input-eq-category').value,
        location: document.getElementById('input-eq-location').value.trim(),
        dailyRate: parseFloat(document.getElementById('input-eq-daily-rate').value),
        hourlyRate: parseFloat(document.getElementById('input-eq-hourly-rate').value),
        ownerName: document.getElementById('input-eq-owner-name').value.trim(),
        ownerContact: document.getElementById('input-eq-owner-contact').value.trim(),
        description: document.getElementById('input-eq-description').value.trim(),
        status: 'AVAILABLE'
    };

    try {
        const res = await fetch('/api/equipment', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            closeModal('modal-add-equipment');
            document.getElementById('form-add-equipment').reset();
            showToast('Agricultural equipment registered successfully!');
            await fetchEquipment();
            await fetchDashboardStats();
        } else {
            showToast('Failed to register equipment. Please check input data.');
        }
    } catch (err) {
        showToast('Network error while saving equipment.');
    }
}

// Submit Book Rental
async function submitBookRental(e) {
    e.preventDefault();
    const payload = {
        equipmentId: parseInt(document.getElementById('book-eq-id').value),
        renterName: document.getElementById('input-renter-name').value.trim(),
        renterContact: document.getElementById('input-renter-contact').value.trim(),
        farmLocation: document.getElementById('input-farm-location').value.trim(),
        startDate: document.getElementById('input-rental-start').value,
        durationDays: parseInt(document.getElementById('input-rental-days').value)
    };

    try {
        const res = await fetch('/api/rentals', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            closeModal('modal-book-rental');
            document.getElementById('form-book-rental').reset();
            showToast('Rental booking submitted! Status: PENDING review by owner.');
            await fetchEquipment();
            await fetchRentals();
            await fetchDashboardStats();
            switchTab('rentals');
        } else {
            const errData = await res.json();
            showToast(errData.error || 'Failed to submit rental reservation.');
        }
    } catch (err) {
        showToast('Network error while reserving equipment.');
    }
}

// Update Rental Workflow Status (approve, reject, dispatch, return)
async function updateRentalStatus(rentalId, action) {
    try {
        const res = await fetch(`/api/rentals/${rentalId}/${action}`, {
            method: 'PUT'
        });

        if (res.ok) {
            showToast(`Rental #${rentalId} successfully updated via action: ${action.toUpperCase()}`);
            await fetchRentals();
            await fetchEquipment();
            await fetchDashboardStats();
        } else {
            const err = await res.json();
            showToast(err.error || 'Action failed.');
        }
    } catch (e) {
        showToast('Network error performing action.');
    }
}

// Utility Toast & Escape
function showToast(msg) {
    const toast = document.getElementById('toast');
    toast.innerText = msg;
    toast.className = 'toast show';
    setTimeout(() => {
        toast.className = 'toast';
    }, 3000);
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;')
              .replace(/</g, '&lt;')
              .replace(/>/g, '&gt;')
              .replace(/"/g, '&quot;')
              .replace(/'/g, '&#039;');
}
